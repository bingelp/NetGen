package com.scires.netgen;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Justin on 2/26/14.
 *
 *
 * @author Justin Robinson
 * @version 0.0.3
 */
public class PanelGroup {
	public JPanel panel;
	public PanelGroup(String groupKey){
		this.panel = new JPanel();
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), groupKey);
		title.setTitleJustification(TitledBorder.LEFT);
		panel.setBorder(title);
		panel.setLayout(new GridLayout(0, 1));
		panel.setMaximumSize(new Dimension(1200, LabeledText.MAX_HEIGHT));
	}
	public JComponent addTo(JComponent component){
		Dimension maxSize = this.panel.getMaximumSize();
		maxSize.setSize(maxSize.getWidth(), maxSize.getHeight()+LabeledText.MAX_HEIGHT);
		this.panel.setMaximumSize(maxSize);
		component.add(this.panel);
		return component;
	}
}
