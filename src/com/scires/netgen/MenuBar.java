package com.scires.netgen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Justin on 3/6/14.
 *
 * Simple menu
 *
 * @author Justin Robinson
 * @version 0.0.1
 */
public class MenuBar extends JMenuBar {

	IPGUI i;
	public MenuBar(){
		JMenu menu;
		JMenuItem menuItem;
		ActionListener openFolder = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseDirectory();
			}
		};

		//File
		menu = new JMenu("File");
		this.add(menu);
		menuItem = new JMenuItem("Open File");
		menu.add(menuItem);
		menuItem = new JMenuItem("OpenFolder");
		menuItem.addActionListener(openFolder);
		menu.add(menuItem);

		//Help
		menu = new JMenu("Help");
		this.add(menu);
		menuItem = new JMenuItem("About");
		menu.add(menuItem);
	}

	public void chooseDirectory(){
		i = (IPGUI) SwingUtilities.getRoot(this);
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("Select root folder containing config files");
		chooser.setAcceptAllFileFilterUsed(false);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			i.directory = chooser.getSelectedFile();
			i.processDirectory();
		}else{
			System.out.println("No directory chosen");
		}
	}
}
