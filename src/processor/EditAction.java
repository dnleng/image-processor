package processor;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;

public class EditAction extends AbstractAction
{   
	private static final long serialVersionUID = 1L;
	private SysLogic logic;
	String request;
	
    public EditAction(SysLogic logic, String name, String tip)
    {   
    	super(name);
        this.logic = logic;
        putValue(Action.SHORT_DESCRIPTION, tip);
    }

    public void actionPerformed(ActionEvent event)
    {   
    	request = (String) this.getValue(Action.NAME);
    	logic.nexus.main.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	
		if(request.equals("Invert"))
			logic.setImage(Kernel.invert(logic.getImage()), logic.zoom, true);
		else if(request.equals("Contrast"))
			logic.setImage(Kernel.contrast(logic.getImage()), logic.zoom, true);
		else if(request.equals("Smoothen Normal"))
			logic.setImage(Kernel.standardKernel(logic.getImage(), Kernel.getSmoothenKernel(9), false), logic.zoom, true);
		else if(request.equals("Smoothen Rough"))
			logic.setImage(Kernel.standardKernel(logic.getImage(), Kernel.getSmoothenKernel(25), false), logic.zoom, true);
		else if(request.equals("Basic Sharpen"))
			logic.setImage(Kernel.standardKernel(logic.getImage(), Kernel.getSharpenKernel(9), true), logic.zoom, true);
		else if(request.equals("Overlay Sharpen"))
			logic.setImage(Kernel.add(logic.getImage(), Kernel.standardKernel(logic.getImage(), Kernel.getSharpenKernel(9), true)), logic.zoom, true);
		else if(request.equals("Sobel"))
			logic.setImage(Kernel.compositeKernel(logic.getImage(), Kernel.getSobel(1), Kernel.getSobel(2)), logic.zoom, true);
		else if(request.equals("Prewitt"))
			logic.setImage(Kernel.compositeKernel(logic.getImage(), Kernel.getPrewitt(1), Kernel.getPrewitt(2)), logic.zoom, true);
		else if(request.equals("Isotropic"))
			logic.setImage(Kernel.compositeKernel(logic.getImage(), Kernel.getIsotropic(1), Kernel.getIsotropic(2)), logic.zoom, true);
    	else if(request.equals("Revert All"))
			logic.reload();
		
		logic.nexus.main.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}