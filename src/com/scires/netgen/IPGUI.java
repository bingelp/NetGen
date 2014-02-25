package com.scires.netgen;

import javax.swing.*;
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
	private ArrayList<JComponent> tabs = null;
	private Parser p = null;
	private File directory = null;

	public IPGUI(){
		textFields = new HashMap<String, LabeledText>();
		chooseDirectory();
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
				tab.setLayout(new GridLayout(0, 2));
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
				addField(e.target, l.tab, e.labelText);
			}
		}
		JScrollPane scrollPane = new JScrollPane(tabbedPane);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(generateButton, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setMinimumSize(new Dimension(600, 200));
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.setMaximumSize( new Dimension( 600, new Double(gd.getDisplayMode().getHeight()*0.7).intValue() ) );
		this.setVisible(true);
	}

	private void addField(String target, int tab, String labelText){
		//check if we already have a field for this
		boolean found = false;
		String key = target+labelText;
		if(textFields.containsKey(key))
			found = true;
		if(!found){
			textFields.put(key, new LabeledText(15, target, labelText));
			this.tabs.set(tab, textFields.get(key).addTo(this.tabs.get(tab)));
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