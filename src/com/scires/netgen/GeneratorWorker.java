package com.scires.netgen;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;


/**
 * Created by Justin on 2/21/14.
 *
 * <P>Thread to handle file generation from files found from {@link com.scires.netgen.ParserWorker}</P>
 *
 * @author Justin Robinson
 * @version 0.1.1
 */
public class GeneratorWorker extends SwingWorker<Integer, Integer>{
	private String inputDirectory	= null;
	ProgressWindow progressWindow;
	private DB db					= null;
	public GeneratorWorker(File directory, ProgressWindow progressWindow, DB db){
		if(directory.isFile())
			directory = directory.getParentFile();
		this.inputDirectory = directory.getAbsolutePath();
		this.progressWindow = progressWindow;
		this.db = db;

	}

	@Override
	protected Integer doInBackground() throws Exception{
		generate();
		return 1;
	}

	@Override
	protected void process(final List<Integer> integers){
		progressWindow.incrementProgress(integers.size());
	}
	private void generate(){
		ArrayList<Map<String, String>> items =  db.getItemsToGenerate();
		String FileID = "";
		Generator generator = new Generator(this.inputDirectory);
		for(Map<String, String> item : items){
			if (!item.get(DB.COL_ID).matches(FileID)) {
				FileID = item.get(DB.COL_ID);
				if(generator.isOpen()){
					generator.close();
				}
				generator.open(item);
			}
			generator.write(item);
		}
		generator.close();
	}
}