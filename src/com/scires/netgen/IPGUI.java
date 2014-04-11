package com.scires.netgen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Justin on 2/21/14.
 *
 * <P>Creates GUI for Cisco config editing based on data from {@link com.scires.netgen.ParserWorker}, then generates
 * changes using {@link com.scires.netgen.GeneratorWorker}</P>
 *
 * @author Justin Robinson
 * @version 0.0.9
 */
public class IPGUI extends JFrame {
	private Map<String, PanelGroup> groups;
	private ArrayList<JPanel> tabs					= null;
	public int[] badFields							= null;
	JTabbedPane tabbedPane							= null;
	JButton generateButton							= null;
	private ParserWorker parserWorker				= null;
	private GeneratorWorker generatorWorker			= null;
	public Map<String, ContainerPanel> containers	= null;
	private ProgressWindow progressWindow			= null;
	public File directory							= null;
	private ActionListener generateAction			= null;
	public static Color GREEN						= new Color(0, 255, 100);
	public static String GENERATED_FOLDER = "Generated";
	public DB db;

	public IPGUI(){
		groups = new HashMap<>();
		this.setTitle("NetGen");
		generateAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cleanDirectory();
				progressWindow.reset(containers.size());
				generatorWorker = new GeneratorWorker(directory, progressWindow, db);
				generatorWorker.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						switch (event.getPropertyName()) {
							case "state":
								switch ((SwingWorker.StateValue) event.getNewValue()) {
									case DONE:
										progressWindow.setVisible(false);
										generatorWorker = null;
										openDirectory();
										break;
									case STARTED:
										progressWindow.setVisible(true);
									case PENDING:
										break;
								}
								break;
						}
					}
				});
				generatorWorker.execute();
			}
		};
		this.add(new MenuBar(), BorderLayout.NORTH);
		this.setMinimumSize(new Dimension(600, 200));

		//
		// Center window
		//
		DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		int width = new Double(dm.getWidth()*0.7).intValue();
		int height = new Double(dm.getHeight()*0.8).intValue();
		Dimension size = new Dimension(width, height);
		this.setPreferredSize(size);
		this.setSize(size);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

		this.pack();
		this.validate();
		this.setAlwaysOnTop(false);
		this.setVisible(true);

		db = new DB();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				db.disconnect();
			}
		});
	}

	public void processDirectory(){
		if(progressWindow == null)
			progressWindow = new ProgressWindow(0);

		parserWorker = new ParserWorker(directory.getAbsolutePath(), progressWindow, this.db);
		parserWorker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				switch (event.getPropertyName()) {
					case "progress":
						progressWindow.setProgress((int)event.getNewValue());
						break;
					case "state":
						switch ((SwingWorker.StateValue) event.getNewValue()) {
							case DONE:
								initTabs();
								containers = parserWorker.containers;
								for (ContainerPanel cp : containers.values()) {
									addPanel(cp);
								}
								generateButton.setVisible(true);
								progressWindow.setVisible(false);
								parserWorker = null;
								break;
							case STARTED:
								progressWindow.setVisible(true);
							case PENDING:
								break;
						}
						break;
				}
			}
		});
		cleanDirectory();
		parserWorker.execute();

		//
		// Make generate button
		//
		if(generateButton == null)
			generateButton = new JButton();
		showGenerateButton();
		generateButton.setVisible(false);

		//
		// Add everything to the frame
		//
		this.add(generateButton, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
	}
	private void cleanDirectory(){
		File generatedDirectory = new File(this.directory + "\\" + IPGUI.GENERATED_FOLDER);
		if(generatedDirectory.exists()){
			String[] files = generatedDirectory.list();
			for(String file: files){
				boolean result = new File(generatedDirectory.getPath(), file).delete();
				if(!result)
					System.out.println("Error deleting file");
			}
		}
	}

	public void hideGenerateButton(){
		if(this.generateButton.getActionListeners().length == 1) {
			this.generateButton.setText("Fix errors before continuing");
			this.generateButton.setBackground(NetGen.COLOR_ERROR);
			this.generateButton.removeActionListener(generateAction);
		}
	}
	public void showGenerateButton(){
		if(this.generateButton.getActionListeners().length == 0) {
			this.generateButton.setText("Generate");
			this.generateButton.setBackground(GREEN);
			this.generateButton.addActionListener(generateAction);
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

		if(!cp.isResized()){
			cp.resize();
			for(Map.Entry<String, ElementPanel> epEntry : cp.elements.entrySet()){
				epEntry.getValue().resize();
			}
		}
	}
	private void initTabs(){
		if(tabbedPane != null)
			this.remove(tabbedPane);
		tabbedPane = new JTabbedPane();
		this.add(tabbedPane, BorderLayout.CENTER);
		tabs = new ArrayList<>();
		for(Field f : ContainerPanel.class.getDeclaredFields()){
			if(f.getModifiers() == Modifier.STATIC){
				JPanel tab = new JPanel();
				tab.setLayout(new BoxLayout(tab, BoxLayout.PAGE_AXIS));
				JScrollPane scrollPane = new JScrollPane(tab);
				tabs.add(tab);
				tabbedPane.addTab(f.getName(), null, scrollPane, f.getName());
				tabbedPane.setBackgroundAt(tabbedPane.getTabCount()-1, NetGen.COLOR_DEFAULT);
			}
		}
		badFields = new int[tabs.size()];
	}


	private void openDirectory(){
		if(Desktop.isDesktopSupported()){
		String directoryPath;
		if(directory.isFile())
			directoryPath = this.directory.getParentFile().getAbsolutePath();
		else
			directoryPath = this.directory.getAbsolutePath();

			File generatedDirectory = new File(directoryPath + "\\" + GENERATED_FOLDER);
			try{
				Desktop.getDesktop().open(generatedDirectory);
			}catch (Exception e){
				System.out.println(ParserWorker.ERROR + e.getMessage());
			}
		}
	}
}