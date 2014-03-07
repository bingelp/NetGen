package com.scires.netgen;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Justin on 3/6/14.
 *
 * <P>JPanel with progress bar api</P>
 *
 * @author Justin Robinson
 * @version 0.0.1
 */
public class ProgressFrame extends JWindow {
	Container container;
	private JProgressBar primaryProgressBar = null;
	private String numFiles					= null;
	private String primaryText				= null;

	public ProgressFrame (int numFiles, String primaryText){
		container = getContentPane();

		this.setMaximumSize(new Dimension(600, 200));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

		this.numFiles = String.valueOf(numFiles);
		this.primaryText = primaryText;
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));

		this.primaryProgressBar = new JProgressBar(0, numFiles);
		this.primaryProgressBar.setString(primaryText + " 0/" + this.numFiles);
		this.primaryProgressBar.setStringPainted(true);
		panel.add(this.primaryProgressBar);

		this.add(panel);
		this.pack();
	}
	public void reset(int n){
		this.primaryProgressBar.setMaximum(n);
		this.primaryProgressBar.setValue(0);
		this.numFiles = String.valueOf(n);
		this.primaryProgressBar.setString(primaryText + " 0/" + this.numFiles);
	}
	public void incrementProgress(int n){
		int newFileNumber = this.primaryProgressBar.getValue()+n;
		this.primaryProgressBar.setValue(newFileNumber);
		String fileNumber = String.valueOf(newFileNumber);
		this.primaryProgressBar.setString(this.primaryText + " " + fileNumber + "/" + this.numFiles);
	}
}
