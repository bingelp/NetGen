package com.scires.netgen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Justin on 3/3/14.
 *
 * <P>Single Element that replaces one string with another.  Contains multiple @{link Location}s defining
 * where the target string is</P>
 *
 * @author Justin Robinson
 * @version 0.0.3
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
	private static String THREE_HOST_IP_OCTETS =
			"\\b(25[0-4]|2[0-4]\\d|[01]?\\d\\d?)" +
			"\\.(25[0-4]|2[0-4]\\d|[01]?\\d\\d?)" +
			"\\.(25[0-4]|2[0-4]\\d|[01]?\\d\\d?)";
	static String IP_GENERIC = THREE_IP_OCTETS+"\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\b";
	static String IP_GENERIC_LINE = ".*"+IP_GENERIC+".*";
	static String IP_HOST = THREE_HOST_IP_OCTETS + "\\.(25[0-4]|2[0-4]\\d||[1]\\d\\d|[1-9]\\d?)\\b";
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
						checkTextField(textField);
					}
					@Override
					public void removeUpdate(DocumentEvent e) {
						checkTextField(textField);
					}
					@Override
					public void changedUpdate(DocumentEvent e) {
						checkTextField(textField);
					}
				});
			}else if( isDate() ){
				final RouterDatePicker rdp = (RouterDatePicker)this.component;
				rdp.getEditor().getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void insertUpdate(DocumentEvent e) {checkDateField(rdp);}
					@Override
					public void removeUpdate(DocumentEvent e) {checkDateField(rdp);}
					@Override
					public void changedUpdate(DocumentEvent e) {checkDateField(rdp);}
				});
			}
		}
		else if ( isCheckBox() ){
			this.trueText = "permit";
			this.falseText = "deny";
			this.originalState = getCheckBox().isSelected();
			final JCheckBox checkBox = (JCheckBox)this.component;
			checkBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IPGUI i = (IPGUI) SwingUtilities.getRoot(checkBox);
					ElementPanel ep = (ElementPanel)checkBox.getParent();
					if(checkBox.isSelected() != ep.originalState) {
						ep.replacement = checkBox.isSelected() ? ep.trueText : ep.falseText;
						i.db.setReplacement(locations, ep.replacement, ep.target);
					}
				}
			});
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
	public JCheckBox getCheckBox(){
		return (JCheckBox)this.component;
	}

	private void checkDateField(RouterDatePicker rdp){
		IPGUI i = (IPGUI) SwingUtilities.getRoot(this);
		if( i != null) {
			String currentText = rdp.getRouterTime();
			boolean changed = !currentText.matches(this.originalText);
			if(changed) {
				i.db.setReplacement(locations, rdp.getRouterTime(), this.target);
			}
		}
	}
	private void checkTextField(JTextField textField){
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

			if(valid) {
				i.db.setReplacement(locations, text, this.target);
				ContainerPanel cp = (ContainerPanel)this.getParent();
				if(cp.tab == ContainerPanel.HOST_NAME) {
						i.db.setOutputFileName(locations, text+".txt");
				}
			}
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
