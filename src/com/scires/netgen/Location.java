package com.scires.netgen;

/**
 * Created by Justin on 2/24/14.
 */
public class Location {
	static int GLOBAL		= 0; //Domain name, name server, secret, username, pwd, vtp pwd, logging
	static int KEY_CHAIN	= 1;
	static int INTERFACE	= 2;
	static int ROUTER		= 3;
	static int ACCESS_LIST	= 4;
	static int NTP_PEER		= 5;

	public int fileIndex, lineNumber, tab;
	public String group;

	public Location(){}

	public Location( int fileIndex, int lineNumber, int tab ){
		this.fileIndex = fileIndex;
		this.lineNumber = lineNumber;
		this.tab = tab;
	}

	public void setFileIndex(int fileIndex){this.fileIndex = fileIndex;}
	public void setLineNumber(int lineNumber){this.lineNumber = lineNumber;}
	public void setTab(int tab){this.tab = tab;}
	public void setGroup(String group){this.group = group;}
}
