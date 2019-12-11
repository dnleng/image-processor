package processor;

import java.awt.*;
import javax.swing.*;

public class Processor extends JFrame
{

	/**
	 * Image Processor
	 * 
	 * Project for Introduction to Image Processing
	 * 
	 * Copyright © 2008-2009, Daniel de Leng
	 */
	private static final long serialVersionUID = 1L;
	
	public Processor()
	{
		// Build window
		this.setSize(800, 600);
		this.setResizable(false);
		this.setTitle("Default Image - Image Processor");
		this.setBackground(Color.LIGHT_GRAY);
		
		try
		{
			ImageIcon image = new ImageIcon(getClass().getResource("/images/icon.gif").toURI().toURL());
			this.setIconImage(image.getImage());
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		this.addWindowListener(new WindowCloser());
		
		// Load program
		System.out.println("Image Processor Copyright 2008-2009, Daniel de Leng");
		this.getContentPane().add(new Nexus(this), BorderLayout.CENTER);
	}
	
	public static void main(String[] args) 
	{
		new Processor().setVisible(true);
	}
}