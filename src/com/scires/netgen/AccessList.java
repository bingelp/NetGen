package com.scires.netgen;

/**
 * Created by Justin on 2/19/14.
 */
public class AccessList{
	public static int DENY = 0;
	public static int PERMIT = 1;
	int state;

	public void setState(int state){
		this.state = state;
	}
}
