package processor;
import java.awt.event.*;

public class WindowCloser extends WindowAdapter 
{
	public void windowClosing(WindowEvent e)
	{
		System.out.println("Terminating...");
		System.exit(0);
	}
}
