package com.scires.netgen;

import javafx.scene.layout.Pane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by Justin on 2/24/14.
 */
public class LabeledText {
	public static int MAX_HEIGHT = 25;
	public JTextField textField= null;
	public JLabel label = null;

	public LabeledText(int size){
		textField = new JTextField(size);
		label = new JLabel();
	}
	public LabeledText(int size, String labelText){
		textField = new JTextField(size);
		label = new JLabel(labelText);
	}
	public LabeledText(int size, String textFieldText, String labelText){
		textField = new JTextField(textFieldText, size);
		label = new JLabel(labelText);
	}

	public String getTarget(){return this.label.getText();}

	public PanelGroup addTo(PanelGroup panelGroup){
		panelGroup.panel.add(this.label);
		panelGroup.panel.add(this.textField);
		return panelGroup;
	}
	public JComponent addTo(JComponent component){
		JPanel p = new JPanel();
		p.add(this.label);
		p.add(this.textField);
/*			TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "LabeledText");
			title.setTitleJustification(TitledBorder.LEFT);
			p.setBorder(title);
		p.setLayout(new FlowLayout(FlowLayout.LEFT));*/
		p.setLayout(new GridLayout(1,2));
		p.setMaximumSize(new Dimension(1200, MAX_HEIGHT));
		component.add(p);
		return component;
	}

}
