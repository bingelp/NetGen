package com.scires.netgen;

/**
 * Created by Justin on 2/24/14.
 *
 * <P>Locations within the config files where text needs to be replaced</P>
 *
 * @author Justin Robinson
 * @version 0.0.3
 */
public class Location {
    public int fileIndex, lineNumber;

    public Location(){}

    public void setFileIndex(int fileIndex){this.fileIndex = fileIndex;}
    public void setLineNumber(int lineNumber){this.lineNumber = lineNumber;}
}
