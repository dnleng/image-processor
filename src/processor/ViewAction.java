package processor;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;

public class ViewAction extends AbstractAction
{   
	private static final long serialVersionUID = 1L;
	private SysLogic logic;
	String request;
	
    public ViewAction(SysLogic logic, String name, String tip)
    {   
    	super(name);
        this.logic = logic;
        putValue(Action.SHORT_DESCRIPTION, tip);
    }

    public void actionPerformed(ActionEvent event)
    {   
    	request = (String) this.getValue(Action.NAME);
    	logic.nexus.main.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		if(request.equals("Normalize"))
			logic.setImage(logic.getImage(), logic.normalZoom, false);
		else if(request.equals("Zoom In"))
			logic.setImage(logic.getImage(), logic.zoom*1.5, false);
		else if(request.equals("Zoom Out"))
			logic.setImage(logic.getImage(), logic.zoom/1.5, false);
		
    	logic.nexus.main.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}