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

	public JPanel addTo(JPanel panel){
		panel.add(this.label);
		panel.add(this.textField);
		return panel;
	}

}
