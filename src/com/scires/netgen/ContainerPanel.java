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

	public ContainerPanel(){
		this.elements = new HashMap<String, ElementPanel>();
	}
}
