package com.scires.netgen;

import javax.swing.*;
import java.io.*;
import java.util.Map;

/**
 * Created by Justin on 3/7/14.
 *
 * <P>Parser Thread to get all files in inputDirectoryPath and parse them using {@link com.scires.netgen.Parser}</P>
 *
 * @author Justin Robinson
 * @version 0.2.0
 *
 */
public class ParserWorker extends SwingWorker<Integer, Integer> {

    private File inputDirectory                     = null;
    private String inputDirectoryPath               = null;
    public String[] files                           = null;
    public ProgressWindow progressWindow            = null;
    private Parser parser                           = null;

    public ParserWorker(String inputDirectoryPath, ProgressWindow progressWindow, DB db){
        this.inputDirectoryPath = inputDirectoryPath;
        this.inputDirectory = new File(inputDirectoryPath);
        this.progressWindow = progressWindow;
        getFiles();
        parser = new Parser(this.inputDirectoryPath, db);
    }

    @Override
    protected Integer doInBackground() throws Exception {
		// Parse each file
        for(String file : files)
            setProgress(parser.parseFile(file));
        return 1;
    }

    public void getFiles(){
		// Returns array of file(s)
		// inputDirectory could be a file or folder so we need to check
        inputDirectory = new File(inputDirectoryPath);
        if(this.inputDirectory.isDirectory()){
            FilenameFilter noGenerateFolder = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !name.matches(GUI.GENERATED_FOLDER);
                }
            };
            files = inputDirectory.list(noGenerateFolder);
        }
        else{
            files = new String[]{inputDirectory.getName()};
            inputDirectory = inputDirectory.getParentFile();
            this.inputDirectoryPath = inputDirectory.getAbsolutePath();
        }
        progressWindow.reset(this.files.length);
    }

    public Map<String, ContainerPanel> getContainers(){
        return this.parser.containers;
    }
    public void close(){
        this.parser = null;
    }



}
