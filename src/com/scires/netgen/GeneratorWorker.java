package com.scires.netgen;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.List;


/**
 * Created by Justin on 2/21/14.
 *
 * <P>Generator for config files based on data from {@link com.scires.netgen.ParserWorker}</P>
 *
 * @author Justin Robinson
 * @version 0.0.8
 */
public class GeneratorWorker extends SwingWorker<Integer, Integer>{
	private Map<String, ContainerPanel> containers	= null;
	private File directory							= null;
	private String[] files							= null;
	ProgressWindow progressWindow;
	private static String TAG						= "GeneratorWorker";
	public GeneratorWorker(Map<String, ContainerPanel> containers, File directory, String[] files, ProgressWindow progressWindow){
		this.containers = containers;
		if(directory.isFile())
			directory = directory.getParentFile();
		this.directory = new File(directory.getAbsolutePath() + "\\Generated");
		this.files = files;
		this.progressWindow = progressWindow;

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
		boolean dirExists = true;
		if (!this.directory.exists()) {
			dirExists = this.directory.mkdir();
		}
		if(dirExists){
			for(Map.Entry<String,ContainerPanel> cpEntry : this.containers.entrySet()){
				ContainerPanel cp = cpEntry.getValue();
				for(ElementPanel ep : cp.elements.values()){
					if(ep.isText()){
						String text = ep.getTextField().getText().trim();
						if ( !text.isEmpty() && text.compareTo(ep.originalText) != 0){
							ep.replacement = text;
							String newFileName = null;
							if (cp.tab == ContainerPanel.HOST_NAME){
								newFileName = ep.replacement;
							}
							writeChange(ep, newFileName);
						}
					}
					else if( ep.isCheckBox() ){
						boolean checked = ep.getCheckBox().isSelected();
						if ( checked != ep.originalState ){
							if ( checked )
								ep.replacement = ep.trueText;
							else
								ep.replacement = ep.falseText;
								writeChange(ep, null);
						}
					}
					else if( ep.isDate() ){
						RouterDatePicker rdp = ep.getDatePicker();
						String replacement = rdp.getRouterTime();
						if( !replacement.matches(ep.originalText) ){
							ep.replacement = replacement;
							writeChange(ep, null);
						}
					}
				}
				publish(0);
			}
		}
	}
	private void writeChange( ElementPanel ep, String newFileName ){
		String target = ep.target;
		String replacement = ep.replacement;
		String line;
		LineNumberReader reader;
		FileOutputStream writer;
		for( Location l : ep.locations ){
			int fileIndex = l.fileIndex;
			int lineNumber = l.lineNumber;
			try {
				String outDirectory = this.directory.getAbsolutePath();
				String inDirectory = this.directory.getParentFile().getAbsolutePath();
				String inFilePath = inDirectory + "\\" + this.files[fileIndex];
				//
				// If a new file name was specified update it in the files array and rename the output file if it exists
				//
				if( newFileName != null ) {
					newFileName = this.files[fileIndex].replace(target, replacement);
					File oldFile = new File(this.directory.getAbsolutePath() + "\\" + this.files[fileIndex]);
					if(oldFile.exists()){
						File newFile = new File(this.directory.getAbsolutePath() + "\\" + newFileName);
						try{
							Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						}catch(Exception e){
							System.out.println(TAG + " " + e.getCause());
						}
					}
					this.files[fileIndex] = newFileName;
				}
				String outFileName = this.files[fileIndex];
				String tempOutFileName = outFileName+".tmp";
				String tempOutFilePath = outDirectory + "\\" + tempOutFileName;
				String outFilePath = outDirectory + "\\" + outFileName;

				//Reads in our generated file from a previous run if it exists,
				//so we don't overwrite our work
				File outFile = new File(outFilePath);
				if (outFile.exists())
					reader = new LineNumberReader(new FileReader(outFilePath));
				else
					reader = new LineNumberReader(new FileReader(inFilePath));

				//Write to a tmp file
				writer = new FileOutputStream(tempOutFilePath);

				//skip lines leading up to the one we need to edit
				for (int linesSkipped = 0; linesSkipped < lineNumber - 1; linesSkipped++)
					writer.write((reader.readLine() + '\n').getBytes());

				//read in line
				//replace target text with replacement
				line = reader.readLine() + '\n';
				line = line.replace(target, replacement);
				writer.write(line.getBytes());
				while ((line = reader.readLine()) != null) {
					writer.write((line + '\n').getBytes());
				}
				writer.flush();
				writer.close();
				reader.close();

				//Rename tmp to txt
				try{
					Files.move(new File(tempOutFilePath).toPath(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}catch(Exception e){
					System.out.println(TAG + " " + e.getCause());
				}

/*				//rename tmp to txt
				if (outFile.exists()){
					boolean result = outFile.delete();
					if(!result)
						System.out.println("Error deleting file");
				}
				boolean result = new File(tempOutFilePath).renameTo(outFile);
				if(!result)
					System.out.println("Error renaming .tmp file");*/
			} catch (Exception e) {
				System.out.println(TAG + " " + ParserWorker.ERROR + e.getMessage());
			}
		}
		// we need to update the target text with our replacement so we can do multiple runs
		// without having the reparse the file(s)
		ep.target = ep.replacement;
		ep.originalText = ep.target;
	}
}