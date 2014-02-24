package com.scires.netgen;

import java.util.ArrayList;

/**
 * Created by Justin on 2/20/14.
 */
public class Router {
	private String id = null;
	private ArrayList<String> networks = null;
	private ArrayList<Route> routes = null;

	public Router(String id){
		this.id = id;
		this.networks = new ArrayList<String>();
		this.routes = new ArrayList<Route>();
	}

	public void addNetwork(String s){
		this.networks.add(s);
	}
	public void addRoute(Route r){
		this.routes.add(r);
	}
}
