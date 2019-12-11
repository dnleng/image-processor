package processor;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;

public class HoughAction extends AbstractAction
{   
	private static final long serialVersionUID = 1L;
	private SysLogic logic;
	String request;
	
    public HoughAction(SysLogic logic, String name, String tip)
    {   
    	super(name);
        this.logic = logic;
        putValue(Action.SHORT_DESCRIPTION, tip);
    }

    public void actionPerformed(ActionEvent event)
    {   
    	request = (String) this.getValue(Action.NAME);
    	logic.nexus.main.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	
		if(request.equals("Hough space"))
			logic.setImage(Kernel.houghSpaceImage(logic.getImage(), true), logic.zoom, true);
		else if(request.equals("Basic lines"))
			logic.setImage(Kernel.houghSpaceImage(logic.getImage(), false), logic.zoom, true);
		else if(request.equals("Line overlay"))
			logic.setImage(Kernel.add(logic.getImage(), Kernel.houghSpaceImage(logic.getImage(), false)), logic.zoom, true);
    
		logic.nexus.main.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}