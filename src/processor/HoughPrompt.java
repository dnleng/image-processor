package processor;
import java.awt.*;
import javax.swing.*;

public class HoughPrompt extends JPanel
{
	private static final long serialVersionUID = 1L;
	JTextField sobelField, houghField;
	JLabel sobelLabel, houghLabel;
	
	public HoughPrompt(int sobelThreshold, int houghThreshold)
	{
		sobelField = new JTextField(Integer.toString(sobelThreshold), 3);
		houghField = new JTextField(Integer.toString(houghThreshold), 3);
		
		sobelLabel = new JLabel("Sobel threshold", JLabel.RIGHT);
		sobelLabel.setLabelFor(sobelField);
		houghLabel = new JLabel("Hough Space threshold", JLabel.RIGHT);
		houghLabel.setLabelFor(houghField);
		
		this.add(sobelLabel, BorderLayout.NORTH);
		this.add(sobelField, BorderLayout.NORTH);
		this.add(houghLabel, BorderLayout.SOUTH);
		this.add(houghField, BorderLayout.SOUTH);
	}
}
