package com.scires.netgen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Justin on 3/3/14.
 *
 * <P>Single Element that replaces one string with another.  Contains multiple @{link Location}s defining
 * where the target string is</P>
 *
 * @author Justin Robinson
 * @version 0.0.1
 */
public class ElementPanel extends MinimumPanel {
	public JLabel label 					= null;
	public JComponent component				= null;
	public ArrayList<Location> locations	= null;
	public String target					= null;
	public String replacement				= null;
	public String regex						= null;
	public String originalText				= null;
	public boolean originalState				  ;
	public String trueText					= null;
	public String falseText					= null;
	public final static Color COLOR_ERROR = new Color(255, 75, 75);
	public final static Color COLOR_DEFAULT = new Color(150, 200, 255);
	private static String THREE_IP_OCTETS =
			"\\b(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)" +
			"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)" +
			"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)";
	static String IP_GENERIC = THREE_IP_OCTETS+"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\b";
	static String IP_GENERIC_LINE = ".*"+IP_GENERIC+".*";
	static String IP_HOST = THREE_IP_OCTETS + "\\.(25[0-5]|2[0-4]\\d|[01]?[1-9]\\d?)\\b";
	static String IP_GATEWAY = THREE_IP_OCTETS + "\\.0";
	static String KEY_TIME =
			"(?:((?:\\d{2}:){2}\\d{2} " +
			"(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) " +
			"\\d{1,2} \\d{4}|infinite))";
	static String COC_PWD = "(?=^.{15,}$)" +
			"(?=.*\\d.*\\d)" +
			"(?=.*[a-z].*[a-z])" +
			"(?=.*[A-Z].*[A-Z])" +
			"(?=.*[!@#$%^;*()_+}{&\":;'?/><].*[!@#$%^;*()_+}{&\":;'?/><])" +
			"(?!.*\\s).*$";
	static String NOT_BLANK = ".{1,}$";

	public ElementPanel(JComponent component, String labelText, Location location, String target, String regex){
		this.label = new JLabel(labelText);
		this.component = component;
		this.locations = new ArrayList<>();
		this.addLocation(location);
		this.target = target;
		this.regex = regex;
		if( isText() || isDate() ){
			this.originalText = target;
			if(this.regex != null && isText()){
				final JTextField textField = (JTextField)this.component;
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
		else if ( isCheckBox() ){
			this.trueText = "permit";
			this.falseText = "deny";
			this.originalState = getCheckBox().isSelected();
		}
		this.make();
	}

	public void make(){
		if(this.label != null)
			this.add(this.label);
		this.add(this.component);
	}

	public void addLocation(Location location){
		this.locations.add(location);
	}

	public boolean isText(){
		return this.component.getClass().toString().matches("class javax.swing.JTextField");
	}
	public boolean isCheckBox(){
		return this.component.getClass().toString().matches("class javax.swing.JCheckBox");
	}
	public boolean isDate(){
		return this.component.getClass().toString().matches("class com.scires.netgen.RouterDatePicker");
	}
	public JTextField getTextField(){
		return (JTextField)this.component;
	}
	public JCheckBox getCheckBox(){
		return (JCheckBox)this.component;
	}
	public RouterDatePicker getDatePicker(){
		return (RouterDatePicker)this.component;
	}

	private void checkField(JTextField textField){
		//
		// Get base pane
		//
		IPGUI i = (IPGUI) SwingUtilities.getRoot(this);
		if(i != null){
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
	}

	private boolean isGood(){
		boolean out = true;
		JTextField textField = (JTextField)this.component;
		if(textField.getBackground() == COLOR_ERROR)
			out = false;
		return out;
	}
}
