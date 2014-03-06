package com.scires.netgen;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Justin on 3/3/14.
 *
 * <P>Container for @{link ElementPanel}s </P>
 */
public class ContainerPanel extends JPanel {
	public Map<String, ElementPanel> elements = null;
	public String group;
	public int tab;

	public ContainerPanel(){
		this.elements = new HashMap<String, ElementPanel>();
		//this.setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	public void make(){
		if (this.tab == Location.ACCESS_LIST || this.tab == Location.INTERFACE){
			this.setLayout(new FlowLayout(FlowLayout.LEFT));
			//this.setMaximumSize(new Dimension(1200, this.elements.size()*(ElementPanel.MAX_HEIGHT)));
		}else{
			this.setLayout(new GridLayout(0,4));
			//int padding = 0;
			//if( this.group != null )
			//	padding = 25;
			//this.setMaximumSize(new Dimension(1200, ElementPanel.MAX_HEIGHT+padding));
		}
	}
}
