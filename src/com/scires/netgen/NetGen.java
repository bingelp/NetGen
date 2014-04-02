package com.scires.netgen;


/**
 * Created by Justin on 2/19/14.
 * <P>Application to import and edit Cisco configuration files</P>
 *
 * @author Justin Robinson
 * @version 0.0.4
 */
public class NetGen {

	public static void main (String[] args){
		String[] split = System.getProperty("java.version").split("\\.");
		float version = Float.parseFloat(split[0]);
		version += Float.parseFloat(split[1])/10;
		if(version >= 1.7)
			new IPGUI();
		else
			new ErrorDialog("Java version 1.7 or greater is required to run NetGen");
	}
}
