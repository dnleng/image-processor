package processor;
import java.awt.*;
import java.util.*;
import javax.swing.*;


public class Nexus extends JApplet
{
	private static final long serialVersionUID = 1L;
	SysLogic logic;
	Processor main;
	JMenuBar menu;
	
	public Nexus(Processor processor)
	{
		// Set pointers
		this.main = processor;
		this.logic = new SysLogic(this);
		
		// Build layout
		this.menu = (JMenuBar)constructMenuBar(constructViewActions(), constructHoughActions());
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(logic, BorderLayout.CENTER);
		c.add(menu, BorderLayout.NORTH);
	}
	
	private Collection<ViewAction> constructViewActions()
	{
		// Construct list of settings
		LinkedList<ViewAction> result;
        result = new LinkedList<ViewAction>();
        result.add( new ViewAction(logic, "Zoom In", "Magnifies without affecting the original image"));
        result.add( new ViewAction(logic, "Zoom Out", "Demagnifies without affecting the original image"));
        result.add( new ViewAction(logic, "Normalize", "Resize image down to 800x600 if larger"));
        return result;
	}
	
    private Collection<HoughAction> constructHoughActions()
    {   
    	// Construct list of game actions
    	LinkedList<HoughAction> result;
    	result = new LinkedList<HoughAction>();
    	result.add( new HoughAction(logic, "Hough space" , "Displays the Hough space of the current image"));
    	result.add( new HoughAction(logic, "Basic lines" , "Displays the Hough lines over a black background"));
    	result.add( new HoughAction(logic, "Line overlay" , "Displays the Hough lines as overlay"));
    	return result;
    }
	
	private Component constructMenuBar(Collection<ViewAction> views, Collection<HoughAction> transforms)
	{
		// Set menu headers
		JMenuBar menubar = new JMenuBar();
		JMenu menu, smoothen, sharpen, edgeDetect, hough;
        
		menu = new JMenu("File");
		menu.add( new FileAction(logic, "Load Image" , "Loads a saved image as greyscale"));
		menu.add( new FileAction(logic, "Save Image" , "Saves current greyscale image to JPEG format"));
		menu.addSeparator();
		menu.add( new FileAction(logic, "Quit" , "Exits the program"));
		menubar.add(menu);
        
		menu = new JMenu("Kernel");
		menu.add(new EditAction(logic, "Invert", "Inverts grey scales"));
		menu.add(new EditAction(logic, "Contrast", "Increases contrast"));
    	
		// Submenu for smoothening operations
		smoothen = new JMenu("Low Pass Filter");
		smoothen.add( new EditAction(logic, "Smoothen Normal", "Using a 3x3 averaging kernel"));
		smoothen.add( new EditAction(logic, "Smoothen Rough", "Using a 5x5 averaging kernel"));
		menu.add( smoothen );
		
		// Submenu for sharpening operations
		sharpen = new JMenu("High Pass Filter");
		sharpen.add( new EditAction(logic, "Basic Sharpen", "Displays the sharpening result"));
		sharpen.add( new EditAction(logic, "Overlay Sharpen", "Applies the sharpening result image to the current image"));
		menu.add( sharpen );
		
		// Submenu for edge detection operations
		edgeDetect = new JMenu("Edge Detection");
		edgeDetect.add( new EditAction(logic, "Sobel", "Applies the Sobel edge detection kernels"));
		edgeDetect.add( new EditAction(logic, "Prewitt", "Applies the Prewitt edge detection kernels"));
		edgeDetect.add( new EditAction(logic, "Isotropic", "Applies the isotropic edge detection kernels"));
		menu.add( edgeDetect );
		
		// Submenu for Hough transformations
		hough = new JMenu("Hough transform");
		for (Action transform : transforms)
			hough.add(transform);
		menu.add(hough);
		
		menu.addSeparator();
		menu.add( new EditAction(logic, "Revert All" , "Reloads original image"));
		
		menubar.add(menu);
		
		menu = new JMenu("View");
		for (Action view : views)
			menu.add(view);
		menubar.add(menu);
	
		menu = new JMenu("Help");
		menu.add(new About());
		menubar.add(menu);
		
		return menubar;
	}
	
	public void adjustWindow(int width, int height)
	{
		main.setSize(width, height);
	}
}
