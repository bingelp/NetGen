package com.scires.netgen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Created by Justin on 3/6/14.
 *
 * <P>JPanel with progress bar api</P>
 */
public class ProgressFrame extends JFrame {
	private JProgressBar primaryProgressBar = null;
	private JProgressBar secondaryProgressBar = null;
	//private JProgressBar ternaryProgressBar = null;
	private String numFiles					= null;
	private String numLinesInFile			= null;
	private String primaryText				= null;
	private String secondaryText			= null;

	public ProgressFrame (int numFiles, String primaryText, String secondaryText/*, String ternaryText*/){
		this.setMaximumSize(new Dimension(600, 200));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		this.setUndecorated(true);

		this.numFiles = String.valueOf(numFiles);
		this.primaryText = primaryText;
		this.secondaryText = secondaryText;
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));

/*		if(ternaryText != null){
			this.ternaryProgressBar = new JProgressBar();
			this.ternaryProgressBar.setString(ternaryText);
			this.ternaryProgressBar.setStringPainted(true);
			this.panel.add(this.ternaryProgressBar);
		}*/
		if(secondaryText != null){
			this.secondaryProgressBar = new JProgressBar();
			this.secondaryProgressBar.setString(secondaryText);
			this.secondaryProgressBar.setStringPainted(true);
			panel.add(this.secondaryProgressBar);
		}

		this.primaryProgressBar = new JProgressBar(0, numFiles);
		this.primaryProgressBar.setString(primaryText + "0/" + this.numFiles);
		this.primaryProgressBar.setStringPainted(true);
		panel.add(this.primaryProgressBar);

		this.add(panel);
		this.pack();
		this.setVisible(true);
	}
	public void secondaryComplete(){
		int newFileNumber = this.primaryProgressBar.getValue()+1;
		this.primaryProgressBar.setValue(newFileNumber);
		String fileNumber = String.valueOf(newFileNumber);
		this.primaryProgressBar.setString(this.primaryText + fileNumber + "/" + this.numFiles);
	}
	public void setSecondaryProgress(int n){
		this.secondaryProgressBar.setValue(n);
		this.secondaryProgressBar.setString(this.secondaryText + String.valueOf(n) + "/" + this.numLinesInFile);
	}
	public void resetSecondary(int n){
		this.secondaryProgressBar.setMaximum(n);
		this.numLinesInFile = String.valueOf(n);
	}

/*	public void ternaryComplete(){

	}
	public void setTernaryProgress(int n){
		this.ternaryProgressBar.setValue(n);
	}*/

	public void close(){
		WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
}
