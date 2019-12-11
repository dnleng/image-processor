package processor;
import java.awt.event.*;
import javax.swing.*;

public class About extends AbstractAction 
{
	private static final long serialVersionUID = 1L;

	public About()
	{
		super("About");
	}
	
	public void actionPerformed(ActionEvent event)
	{
		JOptionPane.showMessageDialog( null
									, "Image Processor\nProject for Introduction to Image Processing, " +
											"Utrecht University\n" +
											"Please consult the provided documentation for more details" +
											"\n\nCopyright 2008-2009, Daniel de Leng (3220540)"
									, "About"
									, JOptionPane.INFORMATION_MESSAGE
									);
	}
}
