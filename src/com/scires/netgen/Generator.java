package com.scires.netgen;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Map;

/**
 * Created by Justin on 2/21/14.
 *
 * <P>Generator for config files based on data from {@link com.scires.netgen.Parser}</P>
 *
 * @author Justin Robinson
 * @version 0.0.3
 */
public class Generator extends Thread{
	private Map<String, Entry> updateables = null;
	private Map<String, LabeledText> textFields = null;
	private File directory = null;
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
			for(Map.Entry<String, LabeledText> tf : this.textFields.entrySet()){
				LabeledText lt = tf.getValue();
				if( !lt.textField.getText().isEmpty()){
					String key = tf.getKey();
					Entry e = this.updateables.get(key);
					e.replacement = lt.textField.getText();
					updateables.put(key, e);
					writeChange(e);
				}
			}
			/*
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
			}*/
		}

	}
	private void cleanDirectory(){
		File generatedDirectory = new File(this.directory + "\\Generated");
		if(generatedDirectory.exists()){
			String[] files = generatedDirectory.list();
			for(String file: files){
				boolean result = new File(generatedDirectory.getPath(), file).delete();
				if(!result)
					System.out.println("Error deleting file");
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
		LineNumberReader reader;
		FileOutputStream writer;
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
					reader = new LineNumberReader(new FileReader(outFilePath));
				else
					reader = new LineNumberReader(new FileReader(inFilePath));

				//Write to a tmp file
				writer = new FileOutputStream(outFilePath+".tmp");

				//skip lines leading up to the one we need to edit
				for (int linesSkipped = 0; linesSkipped < lineNumber - 1; linesSkipped++)
					writer.write((reader.readLine() + '\n').getBytes());

				//read in line
				//replace old ip with new one
				line = reader.readLine() + '\n';
				line = line.replace(target, replacement);
				writer.write(line.getBytes());
				while ((line = reader.readLine()) != null) {
					writer.write((line + '\n').getBytes());
				}
				writer.flush();
				writer.close();
				reader.close();

				//rename tmp to txt
				if (outFile.exists()){
					boolean result = outFile.delete();
					if(!result)
						System.out.println("Error deleting file");
				}
				boolean result = new File(outFilePath+".tmp").renameTo(outFile);
				if(!result)
					System.out.println("Error renaming .tmp file");
			} catch (Exception e) {
				System.out.println(Parser.ERROR + e.getMessage());
			}
		}
	}
}