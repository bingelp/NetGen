package com.scires.netgen;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Justin on 2/21/14.
 */
public class Generator extends Thread{
	private Map<String, Entry> updateables = null;
	private Map<String, LabeledText> textFields = null;
	private File directory = null;
	private LineNumberReader reader = null;
	private FileOutputStream writer = null;
	private String[] files = null;

	public Generator(Map<String, Entry> updateables , Map<String,LabeledText> textFields, File directory, String[] files){
		this.updateables = updateables;
		this.textFields = textFields;
		this.directory = new File(directory.getAbsolutePath() + "\\Generated");
		this.files = files;
	}

	public void run(){
		cleanDirectory();
		generate();
		openDirectory();
	}
	private void generate(){
		boolean dirExists = true;
		if (!this.directory.exists()) {
			dirExists = this.directory.mkdir();
		}
		if(dirExists){
			Iterator it = this.textFields.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry pair = (Map.Entry)it.next();
				LabeledText lt = (LabeledText) pair.getValue();
				if( !lt.textField.getText().isEmpty()){
					String key = pair.getKey().toString();
					Entry e = this.updateables.get(key);
					e.replacement = lt.textField.getText();
					updateables.put(key, e);
					writeChange(e);
				}
			}
		}

	}
	private void cleanDirectory(){
		File generatedDirectory = new File(this.directory + "\\Generated");
		if(generatedDirectory.exists()){
			String[] files = generatedDirectory.list();
			for(String file: files){
				new File(generatedDirectory.getPath(), file).delete();
			}
		}
	}
	private void openDirectory(){
		if(Desktop.isDesktopSupported()){
			try{
				Desktop.getDesktop().open(this.directory);
			}catch (Exception e){
				System.out.println(Parser.ERROR + e.getMessage());
			}
		}
	}
	private void writeChange( Entry entry ){
		String target = entry.target;
		String replacement = entry.replacement;
		String line;
		for( Location l : entry.locations ){
			int fileIndex = l.fileIndex;
			int lineNumber = l.lineNumber;
			try {
				String outFilePath = this.directory.getAbsolutePath() + "\\" + this.files[fileIndex];
				String inFilePath = this.directory.getParentFile().getAbsolutePath() + "\\" + this.files[fileIndex];
				//Reads in our generated file from a previous run if it exists,
				//so we don't overwrite our work
				File outFile = new File(outFilePath);
				if (outFile.exists())
					this.reader = new LineNumberReader(new FileReader(outFilePath));
				else
					this.reader = new LineNumberReader(new FileReader(inFilePath));

				//Write to a tmp file
				this.writer = new FileOutputStream(outFilePath+".tmp");

				//skip lines leading up to the one we need to edit
				for (int linesSkipped = 0; linesSkipped < lineNumber - 1; linesSkipped++)
					this.writer.write((this.reader.readLine() + '\n').getBytes());

				//read in line
				//replace old ip with new one
				line = this.reader.readLine() + '\n';
				line = line.replace(target, replacement);
				this.writer.write(line.getBytes());
				while ((line = this.reader.readLine()) != null) {
					this.writer.write((line + '\n').getBytes());
				}
				this.writer.flush();
				this.writer.close();
				this.reader.close();

				//rename tmp to txt
				if (outFile.exists())
					outFile.delete();
				new File(outFilePath+".tmp").renameTo(outFile);
			} catch (Exception e) {
			}
		}
	}
}