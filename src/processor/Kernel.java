package processor;

import java.util.*;
import java.awt.Color;
import java.awt.image.*;
import javax.swing.*;

public abstract class Kernel
{
	public static BufferedImage copy(BufferedImage input)
	{
		int width, height, xCount, yCount;
		width = input.getWidth();
		height = input.getHeight();
		
		// Construct deep copy of input image
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(yCount = 0 ; yCount < height ; yCount++)
			for(xCount = 0 ; xCount < width ; xCount++)
				result.setRGB(xCount, yCount, input.getRGB(xCount, yCount));
		
		return result;
	}
	
	public static BufferedImage convert(BufferedImage input)
	{
		Color colour;
		int width, height, xCount, yCount, grey;
		width = input.getWidth();
		height = input.getHeight();
		
		// Convert to grey scales
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(yCount = 0 ; yCount < height ; yCount++)
			for(xCount = 0 ; xCount < width ; xCount++)
			{
				colour = new Color(input.getRGB(xCount, yCount));
				grey = (int)Math.floor(0.3*colour.getRed() + 0.59*colour.getGreen() + 0.11*colour.getBlue());	
				result.setRGB(xCount, yCount, new Color(grey, grey, grey).hashCode());
			}

		return result;
	}	
	
	public static BufferedImage invert(BufferedImage input)
	{
		Color oldColor, newColor;
		int xCount, yCount, width, height;
		
		// Generate a copy
		BufferedImage output = copy(input);
		width = input.getWidth();
		height = input.getHeight();
		
		// Invert pixels
		for(yCount = 0 ; yCount < height; yCount++)
			for(xCount = 0 ; xCount < width; xCount++)
			{
				oldColor = new Color(input.getRGB(xCount, yCount));
				newColor = new Color(255-oldColor.getRed(), 255-oldColor.getGreen(), 255-oldColor.getBlue());
				output.setRGB(xCount, yCount, newColor.hashCode());
			}
		
		// Return result
		return output;
	}
	
	public static BufferedImage contrast(BufferedImage input)
	{
		int xCount, yCount, width, height;
		double grey;
		Color colour;
		BufferedImage output;
		ArrayList<Double> list;
		
		// Generate a copy
		list = new ArrayList<Double>();
		width = input.getWidth();
		height = input.getHeight();
		
		// Invert pixels
		for(yCount = 0 ; yCount < height; yCount++)
			for(xCount = 0 ; xCount < width; xCount++)
			{
				colour = new Color(input.getRGB(xCount, yCount));
				grey = 0.3*colour.getRed() + 0.59*colour.getGreen() + 0.11*colour.getBlue();
				list.add(Math.pow(grey, 2));
			}
		
		output = convertList(clamp(list), width, height);
		
		// Return result
		return output;
	}
	
	public static BufferedImage standardKernel(BufferedImage input, ArrayList<Double> kernel, boolean clamp)
	{
		int xCount, yCount, width, height, xReal, yReal, size;
		int xKernel, yKernel, kernelCount, kernelMin, kernelMax;
		double grey = 0.0;
		Color colour;
		ArrayList<Double> list = new ArrayList<Double>();
    	size = kernel.size();
		
		// Test if Kernel is malformed
		if(Math.sqrt(size) != Math.floor(Math.sqrt(size)))
		{
			JOptionPane.showMessageDialog( null
					, "Kernel dimensions are incorrect - operation aborted."
					, "Malformed Kernel"
					, JOptionPane.ERROR_MESSAGE
					);
			return input;
		}
		
		// Generate a copy
		BufferedImage output = copy(input);
		width = input.getWidth();
		height = input.getHeight();
		
		// Set Kernel minimum and maximum
		kernelMax = (int)Math.floor(Math.sqrt(size)/2.0);
		kernelMin = kernelMax*-1;
		
		// Iterate over all pixels
		for(yCount = 0 ; yCount < height; yCount++)
			for(xCount = 0 ; xCount < width; xCount++)
			{
				// Reset grey
				grey = 0;
				
				// Iterate over the current kernel
				xKernel = kernelMin;
				yKernel = kernelMin;
				for(kernelCount = 0 ; kernelCount < size ; kernelCount++)
				{
					// Determine coordinates
					xReal = xCount+xKernel;
					yReal = yCount+yKernel;
					
					// Set grey value
					if(xReal < 0 || yReal < 0 || xReal >= width || yReal >= height)
					{
						if(xReal < 0)
							xReal = 0;
						if(xReal >= width)
							xReal = width-1;
						if(yReal < 0)
							yReal = 0;
						if(yReal >= height)
							yReal = height-1;						
					}
					
					colour = new Color(input.getRGB(xReal, yReal));
					grey += (0.3*colour.getRed() + 0.59*colour.getGreen() + 0.11*colour.getBlue())*kernel.get(kernelCount);
					
					// Kernel coordinate fix
					xKernel++;
					if(xKernel > kernelMax)
					{
						xKernel = kernelMin;
						yKernel++;
					}
				}
				
				// Update output image
				grey = Math.floor(grey);
				if(!clamp)
					output.setRGB(xCount, yCount, new Color((int)grey, (int)grey, (int)grey).hashCode());
				else
					list.add(grey);
			}
		
		if(clamp)
		{
			list = clamp(list);
			for(int count = 0 ; count < list.size() ; count++)
				output.setRGB(count%width, (int)Math.floor(count/width), new Color((int)Math.floor(list.get(count)),(int)Math.floor(list.get(count)),(int)Math.floor(list.get(count))).hashCode());
		}
		
		// Return result
		return output;
	}
	
	public static ArrayList<Double> standardKernelList(BufferedImage input, ArrayList<Double> kernel)
	{
		int xCount, yCount, width, height, xReal, yReal, size;
		int xKernel, yKernel, kernelCount, kernelMin, kernelMax;
		double grey = 0.0;
    	size = kernel.size();
    	Color colour;
    	ArrayList<Double> list = new ArrayList<Double>();
		
		// Test if Kernel is malformed
		if(Math.sqrt(size) != Math.floor(Math.sqrt(size)))
		{
			JOptionPane.showMessageDialog( null
					, "Kernel dimensions are incorrect - operation aborted."
					, "Malformed Kernel"
					, JOptionPane.ERROR_MESSAGE
					);
			return null;
		}
		
		// Generate a copy
		width = input.getWidth();
		height = input.getHeight();
		
		// Set Kernel minimum and maximum
		kernelMax = (int)Math.floor(Math.sqrt(size)/2.0);
		kernelMin = kernelMax*-1;
		
		// Iterate over all pixels
		for(yCount = 0 ; yCount < height; yCount++)
			for(xCount = 0 ; xCount < width; xCount++)
			{
				// Reset grey
				grey = 0;
				
				// Iterate over the current kernel
				xKernel = kernelMin;
				yKernel = kernelMin;
				
				for(kernelCount = 0 ; kernelCount < size ; kernelCount++)
				{
					// Determine coordinates
					xReal = xCount+xKernel;
					yReal = yCount+yKernel;
					
					// Set grey value
					if(xReal < 0 || yReal < 0 || xReal >= width || yReal >= height)
					{
						if(xReal < 0)
							xReal = 0;
						if(xReal >= width)
							xReal = width-1;
						if(yReal < 0)
							yReal = 0;
						if(yReal >= height)
							yReal = height-1;						
					}

					colour = new Color(input.getRGB(xReal, yReal));
					grey += ((colour.getRed()+colour.getGreen()+colour.getBlue())/3)*kernel.get(kernelCount);

					
					// Kernel coordinate fix
					xKernel++;
					if(xKernel > kernelMax)
					{
						xKernel = kernelMin;
						yKernel++;
					}
				}
				
				// Update output image
				list.add(grey);
			}
		
		// Return result
		return list;
	}
	
	public static BufferedImage add(BufferedImage a, BufferedImage b)
	{
		int xCount, yCount, grey, width, height;
		Color aColour, bColour;
		BufferedImage result;
		width = a.getWidth();
		height = a.getHeight();
		result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// Check input
		if(a == null || b == null)
			return null;
		
		// Use BufferedImage a for modification
		for(yCount = 0 ; yCount < height ; yCount++)
			for(xCount = 0 ; xCount < width ; xCount++)
			{
				aColour = new Color(a.getRGB(xCount, yCount));
				bColour = new Color(b.getRGB(xCount, yCount));
				
				grey = (int)Math.floor(((0.3*aColour.getRed() + 0.59*aColour.getGreen() + 0.11*aColour.getBlue())
						+(0.3*bColour.getRed() + 0.59*bColour.getGreen() + 0.11*bColour.getBlue()))/2);
				result.setRGB(xCount, yCount, new Color(grey, grey, grey).hashCode());
			}
		
		// Return result
		return result;

	}
	
	private static ArrayList<Double> clamp(ArrayList<Double> list)
	{
		int count, size;
		double minVal, maxVal, value;
		minVal = 99999999f;
		maxVal = -99999999f;
		size = list.size();
		
		// Find minimum and maximum values
		for(count = 0 ; count < size ; count++)
		{
			value = list.get(count);
			if(value > maxVal)
				maxVal = value;
			if(value < minVal)
				minVal = value;
		}
		
		// Update maximum value
		maxVal -= minVal;
		
		// Clamp values
		for(count = 0 ; count < list.size() ; count++)
		{
			value = (list.get(count)-minVal)*255/maxVal;
			list.set(count, value);
		}
		
		// Return result
		return list;
	}
	
	public static BufferedImage resizeGaussian(BufferedImage input, double ratio)
	{
		int oldWidth, oldHeight, newWidth, newHeight, xCount, yCount, grey;
		double xReal, yReal, floorGrey, ceilingGrey, pointGrey;
		BufferedImage result;
		
		// Determine old and new dimensions
		oldWidth = input.getWidth();
		oldHeight = input.getHeight();
		newWidth = (int)Math.floor(oldWidth*ratio);
		newHeight = (int)Math.floor(oldHeight*ratio);
		
		// Build grey value result image
		result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		
		// Iterate over y-axis
		for(yCount = 0 ; yCount < newHeight ; yCount++)
		{
			// Determine corresponding grid y coordinate
			yReal = yCount/ratio;
			
			// Iterate over x-axis
			for(xCount = 0 ; xCount < newWidth ; xCount++)
			{
				// Some default values
				floorGrey = 255;
				ceilingGrey = 255;
				pointGrey = 255;
				
				// Determine old grid x coordinate
				xReal = xCount/ratio;
				
				// Guard input image precision bounds
				if(Math.ceil(xReal) < oldWidth && Math.ceil(yReal) < oldHeight)
				{
					// Determine y-axis weighed floor gaussian and check for grid alignment
					if(xReal-Math.floor(xReal) != 0)
						floorGrey = (1-(xReal-Math.floor(xReal)))*(new Color(input.getRGB((int)Math.floor(xReal), (int)Math.floor(yReal))).getRed())
							+ (1-(Math.ceil(xReal)-xReal))*(new Color(input.getRGB((int)Math.ceil(xReal), (int)Math.floor(yReal))).getRed());						
					else
						floorGrey = new Color(input.getRGB((int)xReal, (int)Math.floor(yReal))).getRed();
				
					// Determine y-axis weighed ceiling gaussian and check for grid alignment
					if(xReal-Math.floor(xReal) != 0)
						ceilingGrey = (1-(xReal-Math.floor(xReal)))*(new Color(input.getRGB((int)Math.floor(xReal), (int)Math.ceil(yReal))).getRed())
							+ (1-(Math.ceil(xReal)-xReal))*(new Color(input.getRGB((int)Math.ceil(xReal), (int)Math.ceil(yReal))).getRed());				
					else
						ceilingGrey = new Color(input.getRGB((int)xReal, (int)Math.ceil(yReal))).getRed();
				
					// Determine x-axis weighed point gaussian and check for grid alignment
					if(yReal-Math.floor(yReal) != 0)
							pointGrey = (1-(yReal-Math.floor(yReal)))*floorGrey + (1-(Math.ceil(yReal)-yReal))*ceilingGrey;
					else
							pointGrey = new Color(input.getRGB((int)xReal, (int)yReal)).getRed();
					
					// Insert into image
					grey = (int)Math.floor(pointGrey);
					result.setRGB(xCount, yCount, new Color(grey, grey, grey).hashCode());
				}
			}
		}
		
		// Return image with applied ratio
		return result;
	}
	
	public static BufferedImage compositeKernel(BufferedImage input, ArrayList<Double> xKernel, ArrayList<Double> yKernel)
	{
		int width, height, count;
		BufferedImage result;
		ArrayList<Double> list, horizontal, vertical;
		horizontal = new ArrayList<Double>();
		vertical = new ArrayList<Double>();
		list = new ArrayList<Double>();
		width = input.getWidth();
		height = input.getHeight();
		result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// Test if Kernel is malformed
		if(xKernel == null || yKernel == null)
		{
			JOptionPane.showMessageDialog( null
					, "Bad kernel call - operation aborted."
					, "Reference error"
					, JOptionPane.ERROR_MESSAGE
					);
			return null;
		}
		
		// Apply kernels
		horizontal = standardKernelList(input, yKernel);
		vertical = standardKernelList(input, xKernel);
		
		// Build result
		for(count = 0 ; count < width*height ; count++)
			list.add(Math.sqrt(Math.pow(horizontal.get(count), 2)+Math.pow(vertical.get(count), 2) ));
		
		// Convert to BufferedImage
		list = clamp(list);
		result = convertList(list, width, height);
		
		return result;
	}
	
	public static BufferedImage convertList(ArrayList<Double> list, int width, int height)
	{
		int count, grey;
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// Set elements
		for(count = 0 ; count < list.size() ; count++)
		{
			grey = (int)Math.floor(list.get(count));
			result.setRGB(count%width, (int)Math.floor(count/width), new Color(grey, grey, grey).hashCode());
		}
		
		return result;
	}
	
	public static BufferedImage houghSpaceImage(BufferedImage input, boolean returnHoughSpace)
	{
		int count, xCount, yCount, distance, distanceMax, houghThreshold, sobelThreshold, returnVal;
		double angleStep, angle, angleMax, grey, newGrey;
		boolean clear = false;
		ArrayList<Double> hough = new ArrayList<Double>();
		HoughPrompt panel;
		angleStep = 0.5;
		angleMax = 360.0;
		distanceMax = 2*Math.max(input.getWidth(), input.getHeight());
		
		// Set thresholds
		sobelThreshold = 100;
		houghThreshold = 125;
		
		if(!returnHoughSpace)
		{	
			panel = new HoughPrompt(sobelThreshold, houghThreshold);
			while(!clear)
			{
				returnVal = JOptionPane.showConfirmDialog(null, panel, "Requesting thresholds", JOptionPane.OK_CANCEL_OPTION);
				if ( returnVal == JOptionPane.OK_OPTION )
				{
					try
					{
						sobelThreshold = Integer.parseInt(panel.sobelField.getText());
						houghThreshold = Integer.parseInt(panel.houghField.getText());
					}
					catch(Exception e)
					{
						panel = new HoughPrompt(sobelThreshold, houghThreshold);
					}
					
					if(sobelThreshold > 255 || sobelThreshold < 0 || houghThreshold > 255 || houghThreshold < 0)
						panel = new HoughPrompt(sobelThreshold, houghThreshold);
					else
						clear = true;
				}
				else
					return null;
			}
		}
		
		// Build Hough space template
		for(count = 0 ; count < (angleMax/angleStep)*distanceMax ; count++)
			hough.add(0.0);
		
		// Convert image to Sobel
		input = compositeKernel(input, Kernel.getSobel(1), Kernel.getSobel(2));
		
		// Build thresholded Hough space
		for(yCount = 0 ; yCount < input.getHeight() ; yCount++)
			for(xCount = 0 ; xCount < input.getWidth() ; xCount++)
			{
				// Fetch grey value
				grey = new Color(input.getRGB(xCount, yCount)).getRed();
				
				// Iterate over angle list
				if(grey > sobelThreshold || returnHoughSpace)
				for(angle = 0.0 ; angle < angleMax ; angle += angleStep)
				{
					distance = (int)Math.round(xCount*Math.cos(Math.toRadians(angle))+yCount*Math.sin(Math.toRadians(angle)));
					if(distance > 0)
					{
						newGrey = grey + hough.get((int)(angle/angleStep)+(int)(distance*(angleMax/angleStep)));
						hough.set((int)(angle/angleStep)+(int)(distance*(angleMax/angleStep)), newGrey);
					}
				}
			}
		
		// Return hough space if requested
		if(returnHoughSpace)
			return convertList(clamp(hough), (int)(angleMax/angleStep), distanceMax);
		
		// Build result image template
		BufferedImage result = new BufferedImage(input.getWidth(), input.getWidth(), BufferedImage.TYPE_INT_RGB);
		for(yCount = 0 ; yCount < result.getHeight() ; yCount++)
			for(xCount = 0 ; xCount < result.getWidth() ; xCount++)
				result.setRGB(xCount, yCount, Color.black.hashCode());
		
		// Draw final lines
		BufferedImage temp = convertList(clamp(hough), (int)(angleMax/angleStep), distanceMax);
		for(yCount = 0 ; yCount < temp.getHeight() ; yCount++)
			for(xCount = 0 ; xCount < temp.getWidth() ; xCount++)
				if(new Color(temp.getRGB(xCount, yCount)).getRed() > houghThreshold)
				{
					temp.setRGB(xCount, yCount, Color.white.hashCode());
					result = buildHoughLine(result, (xCount*angleStep), yCount, input.getWidth(), input.getHeight());
				}
				else
					temp.setRGB(xCount, yCount, Color.black.hashCode());
		
		// Return line image
		return result;
	}
	
	private static BufferedImage buildHoughLine(BufferedImage input, double angle, double distance, int width, int height)
	{
		int xCount, yCount, store, count;
		store = 0;
		
		for(xCount = 0 ; xCount < width ; xCount++)
			if(Math.sin(Math.toRadians(angle)) != 0)
			{
				// Determine y coordinate
				yCount = (int)Math.round((distance-xCount*Math.cos(Math.toRadians(angle)))/Math.sin(Math.toRadians(angle)));
				if(yCount >=0 && yCount < height)
					input.setRGB(xCount, yCount, Color.white.hashCode());
				
				// Fill in vertical gaps below y coordinate
				for(count = store ; count < yCount ; count++)
					if(count >=0 && count < height)
						input.setRGB(xCount, count, Color.white.hashCode());
				
				// Fill in vertical gaps above y coordinate
				for(count = store ; count > yCount ; count--)
					if(count >=0 && count < height)
						input.setRGB(xCount, count, Color.white.hashCode());
				
				// Store y coordinate for next run
				store = yCount;
			}
		
		return input;
	}
	
	public static ArrayList<Double> threshold(ArrayList<Double> input, int threshold)
	{
		int count;
		double value;
		
		for(count = 0 ; count < input.size() ; count++)
		{
			value = input.get(count);
			if(value > threshold)
				value = 255;
			else
				value = 0;
			
			input.set(count, value);
		}
		
		return input;
	}
	
	public static ArrayList<Double> getSmoothenKernel(int size)
    {
		// Build Kernel
    	ArrayList<Double> kernel = new ArrayList<Double>();
    	for(int count = 0 ; count < size ; count++)
    		kernel.add((double)1/size);
    	
    	return kernel;
    }
    
    public static ArrayList<Double> getSharpenKernel(int size)
    {
		// Build Kernel
    	ArrayList<Double> kernel = new ArrayList<Double>();
    	for(int count = 0 ; count < size ; count++)
    	{
    		if(count == Math.floor(size/2))
    			kernel.add((double)size);
    		else
    			kernel.add(-1.0);
    	}
    	
    	return kernel;
    }
    
    public static ArrayList<Double> getSobel(int kernel)
    {
    	switch(kernel)
    	{
    	case 1:
			// Build k1 kernel
			ArrayList<Double> xKernel = new ArrayList<Double>();
			xKernel.add(-1.0);
			xKernel.add(0.0);
			xKernel.add(1.0);
			xKernel.add(-2.0);
			xKernel.add(0.0);
			xKernel.add(2.0);
			xKernel.add(-1.0);
			xKernel.add(0.0);
			xKernel.add(1.0);
			return xKernel;
    	case 2:
    		// Build k2 kernel
    		ArrayList<Double> yKernel = new ArrayList<Double>();
    		yKernel.add(-1.0);
    		yKernel.add(-2.0);
    		yKernel.add(-1.0);
    		yKernel.add(0.0);
    		yKernel.add(0.0);
    		yKernel.add(0.0);
    		yKernel.add(1.0);
    		yKernel.add(2.0);
    		yKernel.add(1.0);
    		return yKernel;
    	default:
    		return null;
    	}
    }
    
    public static ArrayList<Double> getPrewitt(int kernel)
    {
    	switch(kernel)
    	{
    	case 1:
			// Build k1 kernel
			ArrayList<Double> xKernel = new ArrayList<Double>();
			xKernel.add(-1.0);
			xKernel.add(0.0);
			xKernel.add(1.0);
			xKernel.add(-1.0);
			xKernel.add(0.0);
			xKernel.add(1.0);
			xKernel.add(-1.0);
			xKernel.add(0.0);
			xKernel.add(1.0);
			return xKernel;
    	case 2:
    		// Build k2 kernel
    		ArrayList<Double> yKernel = new ArrayList<Double>();
    		yKernel.add(-1.0);
    		yKernel.add(-1.0);
    		yKernel.add(-1.0);
    		yKernel.add(0.0);
    		yKernel.add(0.0);
    		yKernel.add(0.0);
    		yKernel.add(1.0);
    		yKernel.add(1.0);
    		yKernel.add(1.0);
    		return yKernel;
    	default:
    		return null;
    	}
    }
    
    public static ArrayList<Double> getIsotropic(int kernel)
    {
    	switch(kernel)
    	{
    	case 1:
			// Build k1 kernel
			ArrayList<Double> xKernel = new ArrayList<Double>();
			xKernel.add(-1.0);
			xKernel.add(0.0);
			xKernel.add(1.0);
			xKernel.add(-Math.sqrt(2.0));
			xKernel.add(0.0);
			xKernel.add(Math.sqrt(2.0));
			xKernel.add(-1.0);
			xKernel.add(0.0);
			xKernel.add(1.0);
			return xKernel;
    	case 2:
    		// Build k2 kernel
    		ArrayList<Double> yKernel = new ArrayList<Double>();
    		yKernel.add(-1.0);
    		yKernel.add(-Math.sqrt(2.0));
    		yKernel.add(-1.0);
    		yKernel.add(0.0);
    		yKernel.add(0.0);
    		yKernel.add(0.0);
    		yKernel.add(1.0);
    		yKernel.add(Math.sqrt(2.0));
    		yKernel.add(1.0);
    		return yKernel;
    	default:
    		return null;
    	}
    }
}
