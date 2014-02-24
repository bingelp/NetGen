package com.scires.netgen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Justin on 2/21/14.
 */
public class IPGUI extends JFrame {

	private JPanel panel = null;
	private JButton generateButton = null;
	private LabeledText[] textFields = null;
	private Parser p = null;
	private Map<String, ArrayList<int[]>> IPs = null;
	private String directory = "C:\\Users\\Justin\\git\\NetGen\\data\\SIPR-Configs";

	public IPGUI(){
		p = new Parser(directory);
		p.processFiles();
		IPs = p.getIPs();
		this.setTitle("IPs");
		this.setSize(300, IPs.size() * 50);
		panel = new JPanel();
		textFields = new LabeledText[IPs.size()];
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GenerateThread g = new GenerateThread(IPs, textFields, p);
				g.run();
			}
		});
		int index = 0;
		for(String key : IPs.keySet()){
			textFields[index] = new LabeledText(15, null, key);
			String o = String.valueOf((index+1)*111);
			textFields[index].textField.setText(String.valueOf("192." + o + "." + o + "." + o));
			this.panel = textFields[index].addTo(this.panel);
			index++;
		}
		panel.add(generateButton);
		this.add(panel);
	}
}

class GenerateThread extends Thread{
	private Map<String, ArrayList<int[]>> IPs = null;
	private LabeledText[] textFields = null;
	private Parser p = null;

	public GenerateThread(Map<String, ArrayList<int[]>> IPs, LabeledText[] textFields, Parser p){
		this.IPs = IPs;
		this.textFields = textFields;
		this.p  = p;
	}
	public void run(){
		Object[] keys = IPs.keySet().toArray();
		Hashtable<String, String> newIPss = new Hashtable<String, String>(IPs.size());
		String[] newIPs = new String[IPs.size()*2];
		for(int i=0; i<keys.length; i++){
			int ii = i*2;
			newIPs[ii]=String.valueOf(keys[i]);
			newIPs[ii+1]=textFields[i].textField.getText();
		}
		p.generate(newIPs);
	}
}
