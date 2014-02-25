package com.scires.netgen;

import java.util.ArrayList;

/**
 * Created by Justin on 2/25/14.
 */
public class Entry {
	String target = null;
	String replacement = null;
	String labelText = null;
	ArrayList<Location> locations = null;

	public Entry(){
		locations = new ArrayList<Location>();
	}

	public void setTarget(String target){this.target = target;}
	public void setReplacement(String replacement) {this.replacement = replacement;}
	public void setLabelText(String text){this.labelText = text;}
	public void addLocation(Location location){this.locations.add(location);}
}
