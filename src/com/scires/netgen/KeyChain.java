package com.scires.netgen;

import com.scires.netgen.Key;
import java.util.ArrayList;

/**
 * Created by Justin on 2/19/14.
 */
public class KeyChain {
	public String name = null;
	private ArrayList<Key> keys = null;

	public KeyChain(String name){
		this.name = name;
		this.keys = new ArrayList<Key>();
	}

	public void addKey(Key k){
		this.keys.add(k);
	}

}
