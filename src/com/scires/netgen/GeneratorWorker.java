package com.scires.netgen;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.List;

/**
 * Created by Justin on 2/21/14.
 *
 * <P>Generator for config files based on data from {@link com.scires.netgen.ParserWorker}</P>
 *
 * @author Justin Robinson
 * @version 0.0.5
 */
public class GeneratorWorker extends SwingWorker<Integer, Integer>{
	private Map<String, ContainerPanel> containers	= null;
	private File directory							= null;
	private String[] files							= null;
	ProgressFrame progressFrame;

	public GeneratorWorker(Map<String, ContainerPanel> containers, File directory, String[] files, ProgressFrame progressFrame){
		this.containers = containers;
		this.directory = new File(directory.getAbsolutePath() + "\\Generated");
		this.files = files;
		this.progressFrame = progressFrame;

	}

	@Override
	protected Integer doInBackground() throws Exception{
		generate();
		return 1;
	}

	@Override
	protected void process(final List<Integer> integers){
		progressFrame.incrementProgress(integers.size());
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
				String outFileName;
				if( newFileName != null )
					outFileName = this.files[fileIndex].replace(ep.target, ep.replacement);
				else
					outFileName = this.files[fileIndex];
				String outFilePath = this.directory.getAbsolutePath() + "\\" + outFileName;
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
				System.out.println(ParserWorker.ERROR + e.getMessage());
			}
		}
	}
}