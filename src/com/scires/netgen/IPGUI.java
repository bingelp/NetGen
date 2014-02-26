package com.scires.netgen;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Justin on 2/21/14.
 */
public class IPGUI extends JFrame {
	private JButton generateButton = null;
	private Map<String, LabeledText> textFields;
	private Map<String, PanelGroup> groups;
	private ArrayList<JComponent> tabs = null;
	private Parser p = null;
	private File directory = null;

	public IPGUI(){
		textFields = new HashMap<String, LabeledText>();
		groups = new HashMap<String, PanelGroup>();
		chooseDirectory();
		//directory= new File("C:\\Users\\Justin\\git\\NetGen\\data\\SIPR-Configs");
		if(directory != null){
			processDirectory();
		}
	}

	private void chooseDirectory(){
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(new File("."));
		chooser.setDialogTitle("Select root folder containing config files");
		chooser.setAcceptAllFileFilterUsed(false);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			directory = chooser.getSelectedFile();
		}else{
			System.out.println("No directory chosen");
		}
	}
	private void processDirectory(){
		//Process the config files in the directory
		p = new Parser(directory.getAbsolutePath());
		p.processFiles();
		//Make tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane();
		//Make tabs
		tabs = new ArrayList<JComponent>();
		for(Field f:Location.class.getDeclaredFields()){
			if(f.getModifiers() == 8){
				JComponent tab = new JPanel(false);
				/*if(f.getName().matches("INTERFACE"))
					tab.setLayout(new GridLayout(0, 2));
				else*/
					//tab.setLayout(new GridLayout(0, 1));
					tab.setLayout(new BoxLayout(tab, BoxLayout.PAGE_AXIS));
				tabs.add(tab);
				tabbedPane.addTab(f.getName(), null, tabs.get(tabs.size()-1), f.getName());
			}
		}
		//Setup window
		this.setTitle("NetGen");


		//Make generate button
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Generator g = new Generator(p.getUpdateables(), textFields, directory, p.getFiles());
				g.run();
			}
		});
		//create fields based on config file
		Map<String, Entry> updateables = p.getUpdateables();
		Iterator it = updateables.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			for(Location l : ((Entry) pair.getValue()).locations){
				Entry e = (Entry)pair.getValue();
				addField(e.target, l.tab, e.labelText, l.group);
			}
		}
		JScrollPane scrollPane = new JScrollPane(tabbedPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(generateButton, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setMinimumSize(new Dimension(600, 200));
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.setMaximumSize( new Dimension( 1200, new Double(gd.getDisplayMode().getHeight()*0.7).intValue() ) );
		//this.setLocationRelativeTo(null);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		this.setVisible(true);
	}

	private void addField(String target, int tab, String labelText, String group){
		//if there is a group associated, then add it to that group
		PanelGroup p = null;
		String groupKey = null;
		String key = target+labelText;

		//If we have a group specified, make a key
		//find a the group if we have already created the group
		//if the group isn't there, make a new one
		if(group != null){
			groupKey = group + tab;
			if(!groups.containsKey(groupKey))
				p = new PanelGroup(group);
			else
				p=groups.get(groupKey);
		}
		//If we don't have a textField for this entry, make a new one
		//If it belongs to a group add the text field to that group,
		//If it does not belong to a group, add it straight to the tab
		if(!textFields.containsKey(key)){
			textFields.put(key, new LabeledText(15, target, labelText));
			if( group != null){
				textFields.get(key).addTo(p.panel);
				this.groups.put(groupKey, p);
				this.tabs.set(tab, this.groups.get(groupKey).addTo(this.tabs.get(tab)));
			}else{
				textFields.put(key, new LabeledText(15, target, labelText));
				this.tabs.set(tab, this.textFields.get(key).addTo(this.tabs.get(tab)));
			}
		}
	}

	@Override
	public void paint(Graphics g){
		Dimension d = getSize();
		Dimension m = getMaximumSize();
		boolean resize = d.width > m.width || d.height > m.height;
		d.width = Math.min(m.width, d.width);
		d.height = Math.min(m.height, d.height);
		if (resize) {
			Point p = getLocation();
			setVisible(false);
			setSize(d);
			setLocation(p);
			setVisible(true);
		}
		super.paint(g);
	}
}