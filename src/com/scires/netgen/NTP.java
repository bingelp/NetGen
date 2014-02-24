package com.scires.netgen;

/**
 * Created by Justin on 2/19/14.
 */
public class NTP {
	private String ip = null;
	private int keyID = -1;

	public NTP(String ip, int keyID){
		this.ip=ip;
		this.keyID=keyID;
	}
}
