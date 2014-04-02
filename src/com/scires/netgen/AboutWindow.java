package com.scires.netgen;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Justin on 3/10/14.
 *
 * <P>About window</P>
 *
 * @author Justin Robinson
 * @version 0.0.15
 */
public class AboutWindow extends JDialog {
	Container container;
	String programName = "Netgen";
	String version = "0.0.15";
	private static String TAG = "AboutWindow ";

	public AboutWindow(){
		container = getContentPane();

		int width = 400;
		int height = 400;
		//int titleHeight = 50;
		int bodyHeight = 50;
		Dimension size = new Dimension(width-15, height);
		Dimension bodySize = new Dimension(width-50, bodyHeight);
		Font titleFont = new Font("Arial", Font.BOLD, 20);

		this.setMaximumSize(size);
		this.setSize(size);
		this.setResizable(false);
		this.setTitle("About");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

		JPanel panel = new JPanel();

		JTextArea titleTextArea = new JTextArea(programName + " " + version);
		titleTextArea.setEditable(false);
		titleTextArea.setFont(titleFont);
		titleTextArea.setBackground(this.getBackground());

		JTextArea bodyTextArea = new JTextArea("Updates CISCO router and switch configs\n" +
				"Requires Java Runtime Environment(JRE) 7 or above");
		bodyTextArea.setPreferredSize(bodySize);
		bodyTextArea.setBackground(this.getBackground());


		ImageIcon COCLogo = null;
		try{
			COCLogo = new ImageIcon(this.getClass().getResource("/images/COC_Logo200.png"));
		} catch (Exception e){
			System.out.println(TAG + ParserWorker.ERROR + e.getMessage());
		}
		JLabel COCLogoLabel = new JLabel(COCLogo);


		this.add(panel);
		panel.add(titleTextArea);
		panel.add(bodyTextArea);
		panel.add(COCLogoLabel);
	}
}
