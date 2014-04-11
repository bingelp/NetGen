package com.scires.netgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

/**
 * Created by Justin on 4/9/2014.
 *
 * <P>Handles file generation based on input from {@link com.scires.netgen.GeneratorWorker}</P>
 *
 * @author Justin Robinson
 * @version 0.0.2
 */
public class Generator{

	private File outputDirectory, inputDirectory;
	private String outputFilePath, inputFilePath, tempOutputFilePath;
	private LineNumberReader reader;
	private FileOutputStream writer;
	private boolean dirExists;
	private static String TAG = "Generator ";
	private static String lineSeparator = System.getProperty("line.separator");

	public Generator(String inputDirectoryPath){
		this.inputDirectory = new File(inputDirectoryPath);
		dirExists = this.inputDirectory.exists() || this.inputDirectory.mkdir();
		if(dirExists) {
            outputDirectory = new File(inputDirectoryPath + "\\" + IPGUI.GENERATED_FOLDER);
            if(!outputDirectory.exists()) {
                boolean success = outputDirectory.mkdir();
                if(!success)
                    new ErrorDialog(TAG + "Error created 'Generated' directory");
            }
        }
	}

	public boolean isOpen(){
		return (reader != null && writer != null);
	}
	public void close(){
		readOut();
		finalizeFileName();
		reader = null;
		writer = null;
	}
	public void write(Map<String, String> item){
		goToLine(Integer.valueOf(item.get(DB.colLineNumber)));
		try{
			String line = reader.readLine() + lineSeparator;
			line = line.replace(item.get(DB.colTarget), item.get(DB.colReplacement));
			writer.write(line.getBytes());
		}catch (Exception e){
			new ErrorDialog(TAG + e.getMessage());
		}
	}

	public void open(Map<String, String> item){
		inputFilePath = this.inputDirectory.getAbsolutePath() + "\\" + item.get(DB.colInputFileName);
		outputFilePath =  this.outputDirectory.getAbsolutePath() + "\\" + item.get(DB.colOutputFileName);

		tempOutputFilePath = outputFilePath + ".tmp";
		try{
			writer = new FileOutputStream(tempOutputFilePath);
			reader = new LineNumberReader(new FileReader(inputFilePath));
		}catch (Exception e){
			new ErrorDialog(TAG + e.getMessage());
		}
	}

	private void goToLine(int lineNumber){
		int linesToSkip = lineNumber - reader.getLineNumber() - 1;
		try {
			for (int linesSkipped = 0; linesSkipped < linesToSkip; linesSkipped++)
				writer.write((reader.readLine() + lineSeparator).getBytes());
		}catch (Exception e){
			new ErrorDialog(TAG + e.getMessage());
		}
	}
	private void readOut() {
		String line;
		try {
			while ((line = reader.readLine()) != null)
				writer.write((line + lineSeparator).getBytes());
			writer.flush();
			reader.close();
			writer.close();
		} catch (Exception e) {
			new ErrorDialog(TAG + e.getMessage());
		}
	}
	private void finalizeFileName(){
		try{
			Files.move(new File(tempOutputFilePath).toPath(), new File(outputFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}catch (Exception e){
			new ErrorDialog(TAG + e.getMessage());
		}
	}
}
