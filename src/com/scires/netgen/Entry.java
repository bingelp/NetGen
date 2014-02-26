package com.scires.netgen;

import java.util.ArrayList;

/**
 * Created by Justin on 2/25/14.
 *
 * <P>Contains target and replacement text along with {@link com.scires.netgen.Location } of the targets</P>
 *
 * @author Justin Robinson
 * @version 0.0.3
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
	public void setLabelText(String text){this.labelText = text;}
}
