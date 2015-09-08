package com.iteye.weimingtom.myosotis.stackbasic;

public class Assign {
	private int op;
	private Node value;
	private Node expr;
	
	public Assign(int op, Node value, Node expr) {
		this.op = op;
		this.value = value;
		this.expr = expr;
	}
	
	public void analyze(Compiler c) {
		expr.push(c);
		value.pop(c);
	}
	
	public void push_value(Compiler c) {
		value.push(c);
	}
	
	public void pop_value(Compiler c) {
		value.pop(c);
	}

}
