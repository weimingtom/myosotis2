package com.iteye.weimingtom.myosotis.stackbasic;

import java.util.Iterator;
import java.util.LinkedList;

public class Args {
	private LinkedList<Node> args = new LinkedList<Node>();
	
	public Args(Node node) {
		args.add(node);
	}
	
	public Args Add(Node node) {
		args.add(node);
		return this;
	}
	
	public int getSize() { 
		return args.size(); 
	}
	
	public Node get(int idx) { 
		return args.get(idx); 
	}
	
	public Iterator<Node> getIterator() {
		return args.iterator();
	}

	public Iterator<Node> getReverseIterator() {
		return args.descendingIterator();
	}
}
