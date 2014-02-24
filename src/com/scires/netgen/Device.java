package com.scires.netgen;

import java.util.ArrayList;

/**
 * Created by Justin on 2/19/14.
 */
public class Device {
	private String hostname = null;
	private ArrayList<KeyChain> keyChains = null;
	private ArrayList<Interface> interfaces = null;
	private ArrayList<Router> routers = null;
	private ArrayList<AccessList> accessLists = null;
	private ArrayList<NTP> ntps = null;

	public Device(String hostname){
		this.hostname = hostname;
		this.keyChains = new ArrayList<KeyChain>();
		this.interfaces = new ArrayList<Interface>();
		this.routers = new ArrayList<Router>();
		this.accessLists = new ArrayList<AccessList>();
		this.ntps = new ArrayList<NTP>();
	}

	public void addKeyChain(KeyChain kc){this.keyChains.add(kc);}
	public void addInterface(Interface i){this.interfaces.add(i);}
	public void addRouter(Router r){this.routers.add(r);}
	public void addAccessList(AccessList al){this.accessLists.add(al);}
	public void addNTP(NTP n){this.ntps.add(n);}
}
