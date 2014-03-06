package com.scires.netgen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Justin on 2/21/14.
 *
 * <P>Creates GUI for Cisco config editing based on data from {@link com.scires.netgen.Parser}, then generates
 * changes using {@link com.scires.netgen.Generator}</P>
 *
 * @author Justin Robinson
 * @version 0.0.5
 */
public class IPGUI extends JFrame {
	private Map<String, PanelGroup> groups;
	private ArrayList<JPanel> tabs = null;
	public int[] badFields = null;
	JTabbedPane tabbedPane = null;
	JButton generateButton = null;
	private Parser p = null;
	private File directory = null;

	public IPGUI(){
		groups = new HashMap<String, PanelGroup>();
		chooseDirectory();
		//directory = new File("C:\\Users\\Justin\\git\\NetGen\\data\\SIPR-Configs");
		processDirectory();
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
		p = new Parser(directory.getAbsolutePath());
		p.processFiles();
		tabbedPane = new JTabbedPane();
		//
		// Make tabs from static variables in Location class
		//
		tabs = new ArrayList<JPanel>();
		for(Field f : Location.class.getDeclaredFields()){
			if(f.getModifiers() == Modifier.STATIC){
				JPanel tab = new JPanel();
				tab.setLayout(new BoxLayout(tab, BoxLayout.Y_AXIS));
				JScrollPane scrollPane = new JScrollPane(tab);
				tabs.add(tab);
				tabbedPane.addTab(f.getName(), null, scrollPane, f.getName());
				tabbedPane.setBackgroundAt(tabbedPane.getTabCount()-1, ElementPanel.COLOR_DEFAULT);
			}
		}
		badFields = new int[tabs.size()];

		//
		// Make generate button
		//
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Generator g = new Generator(p.containers, directory, p.getFiles());
				g.run();
			}
		});

		//
		// Add everything to the frame
		//
		this.setTitle("NetGen");
		this.add(tabbedPane, BorderLayout.CENTER);
		this.add(generateButton, BorderLayout.SOUTH);
		for(ContainerPanel cp : p.containers.values()){
			addPanel(cp);
		}

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		this.setMinimumSize(new Dimension(600, 200));

		//
		// Center window
		//
		DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		//this.setMaximumSize( new Dimension( gd.getDisplayMode().getWidth()+100, new Double(gd.getDisplayMode().getHeight()*0.98).intValue() ) );
		int width = new Double(dm.getWidth()*0.7).intValue();
		int height = new Double(dm.getHeight()*0.8).intValue();
		Dimension size = new Dimension(width, height);
		this.setPreferredSize(size);
		this.setSize(size);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);


		this.validate();
		this.setVisible(true);
	}

	public void hideGenerateButton(){
		if(this.generateButton != null)
		this.generateButton.setVisible(false);
	}
	public void showGenerateButton(){
		if(this.generateButton != null)
			this.generateButton.setVisible(true);
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
	public void addPanel(ContainerPanel cp){
		PanelGroup pg = null;
		String groupKey;
		JPanel tab = this.tabs.get(cp.tab);

		if(cp.group != null){
			groupKey = cp.group + cp.tab;
			if(!groups.containsKey(groupKey))
				pg = new PanelGroup(cp.group);
			else
				pg = groups.get(groupKey);
			pg.add(cp);
			this.groups.put(groupKey, pg);
			tab.add(pg);
		}else{
			tab.add(cp);
		}

		this.tabs.set(cp.tab, tab);
		this.pack();
		if( pg != null)
			pg.resize();
		cp.resize();
		for(Map.Entry<String, ElementPanel> epEntry : cp.elements.entrySet()){
			epEntry.getValue().resize();
		}
	}
}