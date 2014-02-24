package com.scires.netgen;

/**
 * Created by Justin on 2/19/14.
 */
public class Interface {
	public String name = null;
	public String ip = null;
	public String mask = null;

	public Interface(String name){
		this.name = name;
	}
	public void setIP(String ip){
		this.ip = ip;
	}
	public void setMask(String mask){
		this.mask = mask;
	}
}
