package com.scires.netgen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
	public final static Color COLOR_ERROR = new Color(255, 75, 75);
	public final static Color COLOR_DEFAULT = new Color(150, 200, 255);
	String regex = null;
	public String originalText = null;

	public LabeledText(int size, String textFieldText, String labelText, String r){
		this.textField = new JTextField(textFieldText, size);
		this.originalText = textFieldText;
		this.label = new JLabel(labelText);
		this.regex = r;

		//If a regex value is specified, add an ActionListener to
		//check the text against the regex
		if(this.regex != null){
			textField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					checkField(textField);
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					checkField(textField);
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					checkField(textField);
				}
			});
		}
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

	private void checkField(JTextField textField){
		//
		// Get base pane
		//
		Container c = textField.getParent();
		IPGUI i;
		while((c.getParent()) != null)
			c=c.getParent();
		i=(IPGUI)c;
		String text = textField.getText();
		int activeTab = i.tabbedPane.getSelectedIndex();
		boolean wasValid = this.isGood();
		boolean valid = text.matches(this.regex);


		if(valid && !wasValid){
			textField.setBackground(Color.WHITE);
			//i.tabbedPane.setBackgroundAt(activeTab, COLOR_DEFAULT);
			i.badFields[activeTab]--;
		}
		else if(!valid && wasValid){
			textField.setBackground(COLOR_ERROR);
			//i.tabbedPane.setBackgroundAt(activeTab, COLOR_ERROR);
			i.badFields[activeTab]++;
		}
		if(i.badFields[activeTab] < 0)
			i.badFields[activeTab] = 0;
		if(i.badFields[activeTab] > 0)
			i.tabbedPane.setBackgroundAt(activeTab, COLOR_ERROR);
		else
			i.tabbedPane.setBackgroundAt(activeTab, COLOR_DEFAULT);
		int totalBadFields = 0;
		for(int j : i.badFields)
			totalBadFields += j;

		if (totalBadFields == 0)
			i.showGenerateButton();
		else
			i.hideGenerateButton();
	}

	private boolean isGood(){
		boolean out = true;
		if(this.textField.getBackground() == COLOR_ERROR)
			out = false;
		return out;
	}

}
