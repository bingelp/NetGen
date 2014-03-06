package com.scires.netgen;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Justin on 3/3/14.
 *
 * <P>Container for @{link ElementPanel}s </P>
 */
public class ContainerPanel extends MinimumPanel {
	public Map<String, ElementPanel> elements = null;
	public String group;
	public int tab;

	static int GLOBAL		= 0; //Domain name, name server, secret, username, pwd, vtp pwd, logging
	static int HOST_NAME	= 1;
	static int KEY_CHAIN	= 2;
	static int INTERFACE	= 3;
	static int ROUTER		= 4;
	static int ACCESS_LIST	= 5;
	static int NTP_PEER		= 6;

	public ContainerPanel(){
		this.elements = new HashMap<String, ElementPanel>();
	}

	@Override
	public void resize(){
		super.resize();
		this.resized = true;
	}
}
