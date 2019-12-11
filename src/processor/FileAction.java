package processor;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;

public class FileAction extends AbstractAction
{   
	private static final long serialVersionUID = 1L;
	private SysLogic logic;
	String request;
	
    public FileAction(SysLogic logic, String name, String tip)
    {   
    	super(name);
        this.logic = logic;
        putValue(Action.SHORT_DESCRIPTION, tip);
    }

    public void actionPerformed(ActionEvent event)
    {   
    	request = (String) this.getValue(Action.NAME);
    	logic.nexus.main.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
    	if(request.equals("Load Image") || request.equals("Save Image"))
    	{
    		FileLoader fileLoader = new FileLoader(logic);
            fileLoader.actionPerformed(request);
    	}
    	else if(request.equals("Quit"))
    	{
    		System.out.println("Terminating...");
    		System.exit(0);
    	}
    	
    	logic.nexus.main.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}