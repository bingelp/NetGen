package com.scires.netgen;

/**
 * Created by Justin on 2/19/14.
 */
public class Key {
	public int number;
	public String keyString = null;
	public String acceptLifetime = null;
	public String sendLifetime = null;

	public Key(int number){
		this.number = number;
	}
	public void setKeyString(String s){
		this.keyString=s;
	}
	public void setAcceptLifetime(String s){
		this.acceptLifetime = s;
	}
	public void setSendLifetime(String s){
		this.sendLifetime = s;
	}
}
