package com.scires.netgen;

/**
 * Created by Justin on 2/20/14.
 */
public class Route {
	private String prefix = null;
	private String mask = null;
	private String ip = null;

	public Route(String prefix, String mask ,String ip){
		this.prefix = prefix;
		this.mask = mask;
		this.ip = ip;
	}
}
