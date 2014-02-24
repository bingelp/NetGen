package com.scires.netgen;

/**
 * Created by Justin on 2/19/14.
 */
public class AccessList extends BaseEntry{
	public static int DENY = 0;
	public static int PERMIT = 1;
	public String oldIP, newIP;
	int state;
	public AccessList(int fileIndex, int lineNumber, int lineIndex, String oldIP, String newIP, int state){
		super(fileIndex, lineNumber, lineIndex);
		this.oldIP = oldIP;
		this.newIP = newIP;
		this.state = state;
	}
}
