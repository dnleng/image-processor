package processor;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileLoader extends JPanel
{
	private static final long serialVersionUID = 1L;
	JFrame frame;
	JFileChooser chooser;
	SysLogic logic;
	FileNameExtensionFilter loadFilter, saveFilter;
	
	public FileLoader(SysLogic logic)
	{
		// Build frame
		this.logic = logic;
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// Build menu for choosing files
		chooser = new JFileChooser();
		loadFilter = new FileNameExtensionFilter("Image Files", "jpg", "gif", "png", "bmp", "tiff");
		saveFilter = new FileNameExtensionFilter("JPEG Format", "jpg");
	}

	public void actionPerformed(String action) 
	{
		int returnVal, proceed;
		File file;
		String source;
		
        // Determine what file action was called
        if (action.equals("Load Image"))
        {
        	chooser.setFileFilter(loadFilter);
            returnVal = chooser.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
            	// Load file
                file = chooser.getSelectedFile();
                logic.loadImage(file);
            }
            else
            	frame.dispose();
        }
        else if (action.equals("Save Image")) 
        {
        	chooser.setFileFilter(saveFilter);
            returnVal = chooser.showSaveDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                file = chooser.getSelectedFile();
                source = file.toString();
                
                try
                {
                	if (!source.toString().endsWith(".jpg"))
                		file = new File(source+".jpg");
                	
                	if(file.exists())
        				proceed = JOptionPane.showConfirmDialog(null
        						, "The specified file location already exists. Do you wish to overwrite?"
        						, "File already exists"
        						, JOptionPane.YES_NO_OPTION);
                	else
                		proceed = 0;
                	
    				if(proceed == 0)
    				{
                    	ImageIO.write(Kernel.convert(Kernel.convert(logic.getImage())), "jpg", file);
            			JOptionPane.showMessageDialog( null
            					, "Image saved succesfully to "+file.getPath()
            					, "Saved succesfully"
            					, JOptionPane.INFORMATION_MESSAGE
            					);
    				}
                }
                catch(Exception e)
                {
                	System.out.println(e);
        			JOptionPane.showMessageDialog( null
        					, "Unable to write image to specified location."
        					, "Writing error"
        					, JOptionPane.ERROR_MESSAGE
        					);
                }
            }
            else
            	frame.dispose();
        }

	}
}
