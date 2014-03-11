package com.scires.netgen;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Justin on 3/6/14.
 *
 * <P>JPanel with progress bar api</P>
 *
 * @author Justin Robinson
 * @version 0.0.2
 */
public class ProgressWindow extends JWindow {
	Container container;
	private JProgressBar primaryProgressBar = null;

	public ProgressWindow(int numFiles){
		container = getContentPane();

		this.setMaximumSize(new Dimension(600, 200));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));

		this.primaryProgressBar = new JProgressBar(0, numFiles);
		panel.add(this.primaryProgressBar);

		this.add(panel);
		this.pack();
	}
	public void reset(int n){
		this.primaryProgressBar.setMaximum(n);
		this.primaryProgressBar.setValue(0);
	}
	public void incrementProgress(int n){
		int newFileNumber = this.primaryProgressBar.getValue()+n;
		this.primaryProgressBar.setValue(newFileNumber);
	}
	public void setProgress(int n){
		this.primaryProgressBar.setValue(n);
	}
}
