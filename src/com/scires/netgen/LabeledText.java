package com.scires.netgen;

import javax.swing.*;

/**
 * Created by Justin on 2/24/14.
 */
public class LabeledText {
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

	public JPanel addTo(JPanel panel){
		panel.add(this.label);
		panel.add(this.textField);
		return panel;
	}
	public JComponent addTo(JComponent component){
		component.add(this.label);
		component.add(this.textField);
		return component;
	}

}
