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
    private JProgressBar progressBar	= null;

    public ProgressWindow(int numFiles, Point p, Dimension guiSize){
        container = getContentPane();
		//width is 60% of main window
		int width = (int)(guiSize.width * 0.6);
		int height = width/10;

		//center on main screen
		int x = p.x + guiSize.width/2 - width/2;
		int y = p.y + guiSize.height/2 - height/2;
        this.setLocation(x,y);

		// add progressBar to panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0,1));
        this.progressBar = new JProgressBar(0, numFiles);
		this.progressBar.setPreferredSize(new Dimension(width, height));
        panel.add(this.progressBar);

		// add panel to this
        this.add(panel);
		this.setAlwaysOnTop(true);
        this.pack();
    }
    public void reset(int n){
		//reset value to 0 and max value to n
        this.progressBar.setMaximum(n);
        this.progressBar.setValue(0);
    }
    public void incrementProgress(int n){
		// increment progress bar by n
        this.progressBar.setValue(this.progressBar.getValue()+n);
    }
    public void setProgress(int n){
		// set progress to a specific value
        this.progressBar.setValue(n);
    }
}
