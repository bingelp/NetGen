package com.scires.netgen;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Justin on 3/6/14.
 *
 * <P>JPanel that will not get larger than it's minimum size</P>
 *
 * @author Justin Robinson
 * @version 0.0.5
 */
public class MinimumPanel extends JPanel {

	public MinimumPanel(){
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	public void resize(){
		Dimension minHeight = this.getMinimumSize();
		DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		minHeight.setSize(dm.getWidth(), minHeight.getHeight());
		this.setMaximumSize(minHeight);
	}
}
