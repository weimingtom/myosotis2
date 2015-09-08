package com.iteye.weimingtom.myosotis.memorybasic;

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
	
	public void analyze(Compiler c) {
		Iterator<Node> it = args.iterator();
		while(it.hasNext()) {
			Node node = (Node) it.next();
			node.analyze(c);
		}
	}
	
	public Iterator<Node> getIterator() {
		return args.iterator();
	}

	public Iterator<Node> getReverseIterator() {
		return args.descendingIterator();
	}
}
