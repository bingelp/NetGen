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
 * @version 0.0.3
 */
public class MenuBar extends JMenuBar {

    GUI i;
    AboutWindow aboutWindow;

    private enum Action {
        FILE, DIRECTORY
    }
    public MenuBar(){
        JMenu menu;
        JMenuItem menuItem;
        ActionListener openFolder = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory(Action.DIRECTORY);
            }
        };
        ActionListener openFile = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory(Action.FILE);
            }
        };
        ActionListener openAbout = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (aboutWindow == null){
                    aboutWindow = new AboutWindow();
                }
                aboutWindow.setVisible(true);
            }
        };

        //File
        menu = new JMenu("File");
        this.add(menu);
        menuItem = new JMenuItem("Open File");
        menuItem.addActionListener(openFile);
        menu.add(menuItem);
        menuItem = new JMenuItem("Open Folder");
        menuItem.addActionListener(openFolder);
        menu.add(menuItem);

        //Help
        menu = new JMenu("Help");
        this.add(menu);
        menuItem = new JMenuItem("About");
        menu.add(menuItem);
        menuItem.addActionListener(openAbout);
    }

    public void chooseDirectory(Action action){
		// Opens a file/folder picker dialog based on action
        int option = -1;
        String title = null;
        i = (GUI) SwingUtilities.getRoot(this);
        JFileChooser chooser = new JFileChooser();
        switch (action){
            case FILE:
				option = JFileChooser.FILES_ONLY;
                title = "Open file";
                break;
            case DIRECTORY:
                option = JFileChooser.DIRECTORIES_ONLY;
                title = "Select root folder containing config files";
                break;
        }
		//apply file or folder option
        chooser.setFileSelectionMode(option);
		//set current directory of picker to the directory of NetGen.jar
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle(title);
        chooser.setAcceptAllFileFilterUsed(false);
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            i.directory = chooser.getSelectedFile();
            i.processDirectory();
        }else{
            System.out.println("No directory chosen");
        }
    }
}
