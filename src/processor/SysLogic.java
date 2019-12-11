package processor;
import java.awt.*;
import java.text.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import java.io.IOException;
import java.net.*;

public class SysLogic extends JPanel
{
	private static final long serialVersionUID = 1L;
	private static final int MENU = 50;
	private BufferedImage image, backup, visual;
	Nexus nexus;
	double zoom, normalZoom;
	String fileName;
	
	public SysLogic(Nexus nexus)
	{
		this.nexus = nexus;
		this.loadDefault();
	}
	
	public void paint(Graphics gr)
	{
		int x, y;
		x = (this.getWidth()/2)-(visual.getWidth()/2);
		y = (this.getHeight()/2)-(visual.getHeight()/2);
		gr.drawImage(this.visual, x, y, null);
	}
	
	public void setImage(BufferedImage input, double ratio, boolean kernelChange)
	{
		if(input != null)
		{
			if(ratio != 1.0)
				this.visual = Kernel.resizeGaussian(input, ratio);
			else
				this.visual = input;
			this.zoom = ratio;
			this.image = input;
			
			// Adjust window and draw image
			nexus.adjustWindow(visual.getWidth(), visual.getHeight()+MENU);
			this.fixTitle();
			this.repaint();
		}
	}
	
	public BufferedImage getImage()
	{
		return this.image;
	}
	
	public BufferedImage getVisual()
	{
		return this.visual;
	}	
	
	public void loadDefault()
	{
		// Load standard image
		try
		{
			URL url = getClass().getResource("/images/default.jpg").toURI().toURL();
			this.fileName = "Default Image";
			BufferedImage img = ImageIO.read(url);
			
			double zoom;
			if(img.getHeight() > 600 || img.getWidth() > 800)
			{
				zoom = Math.max(600.0/img.getHeight(), 800.0/img.getWidth());
				this.normalZoom = zoom;
			}
			else
			{
				zoom = 1.0;
				this.normalZoom = Math.max(600.0/img.getHeight(), 800.0/img.getWidth());
			}
			
			this.setImage(Kernel.convert(img), zoom, false);
			this.backup(Kernel.copy(getImage()));
			
			// Set window title
			fixTitle();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	public void loadImage(File source)
	{
		int proceed = 0;
		boolean warn = false;
		
		nexus.main.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try
		{	
			// Load image
			this.fileName = source.getName().toString();
			BufferedImage img = ImageIO.read(source);
			
			// Check for recommended dimensions
			if(img.getWidth()*img.getHeight() > 800*600)
			{
				warn = true;
				proceed = JOptionPane.showConfirmDialog(null, "The image you're attempting to load exceeds " +
						"the recommended dimensions\n and may crash this application under heavy workload." +
						"\n\nDo you wish to proceed anyway?"
						, "Warning"
						, JOptionPane.YES_NO_OPTION);
			}
			
			if(!warn || proceed == 0)
			{		          
				double zoom;
				if(img.getHeight() > 600 || img.getWidth() > 800)
				{
					zoom = Math.max(600.0/img.getHeight(), 800.0/img.getWidth());
					this.normalZoom = zoom;
				}
				else
				{
					zoom = 1.0;
					this.normalZoom = Math.max(600.0/img.getHeight(), 800.0/img.getWidth());
				}
				
				this.setImage(Kernel.convert(ImageIO.read(source)), zoom, false);
				this.backup(Kernel.copy(getImage()));
				this.normalZoom = zoom;
				
				// Set window title
				this.fixTitle();
			}
		}
		catch(IOException e)
		{
        	// Notify user
        	System.out.println("Unable to open specified image file");
			JOptionPane.showMessageDialog( null
										, "Unable to open specified image file"
										, "Error"
										, JOptionPane.ERROR_MESSAGE
										);
		}
		nexus.main.setCursor(Cursor.getDefaultCursor());
	}
	
	public void setTitle(String title)
	{
		this.nexus.main.setTitle(title);
	}
	
	public void reload()
	{
		this.setImage(this.backup, normalZoom, false);
	}
	
	public void backup(BufferedImage image)
	{
		this.backup = image;
	}
	
	private void fixTitle()
	{
		DecimalFormat Percentage = new DecimalFormat("#0.00%");
		this.setTitle(this.fileName+" ("+image.getWidth()+"x"+image.getHeight()+") "+Percentage.format(this.zoom)+" - Image Processor");
	}
}