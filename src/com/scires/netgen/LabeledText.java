package com.scires.netgen;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Justin on 2/24/14.
 *
 * <P>Simple JLabel and JTextField combination</P>
 *
 * @author Justin Robinson
 * @version 0.0.3
 */
public class LabeledText {
	public static int MAX_HEIGHT = 25;
	public JTextField textField= null;
	public JLabel label = null;

	public LabeledText(int size, String textFieldText, String labelText){
		textField = new JTextField(textFieldText, size);
		label = new JLabel(labelText);
	}

	public JComponent addTo(JComponent component){
		JPanel p = new JPanel();
		p.add(this.label);
		p.add(this.textField);
		p.setLayout(new GridLayout(1,2));
		p.setMaximumSize(new Dimension(1200, MAX_HEIGHT));
		component.add(p);
		return component;
	}

}
