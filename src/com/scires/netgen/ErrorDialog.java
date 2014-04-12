package com.scires.netgen;

import javax.swing.*;

/**
 * Created by Justin on 4/2/2014.
 * <P>ErrorDialog that takes text as in input</P>
 *
 * @author Justin Robinson
 * @version 0.0.1
 */
public class ErrorDialog extends JFrame {
	public ErrorDialog(String errorText){
		JOptionPane.showMessageDialog(this, errorText, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
