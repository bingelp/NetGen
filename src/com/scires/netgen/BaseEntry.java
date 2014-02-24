package com.scires.netgen;

/**
 * Created by Justin on 2/24/14.
 */
public class BaseEntry {
	public int fileIndex, lineNumber, lineIndex;

	public BaseEntry(int fileIndex, int lineNumber, int lineIndex){
		this.fileIndex = fileIndex;
		this.lineNumber = lineNumber;
		this.lineIndex = lineIndex;
	}
}
