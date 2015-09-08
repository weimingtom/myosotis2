package com.iteye.weimingtom.myosotis.stackbasic;

public class StackOverflowException extends Exception {
	private static final long serialVersionUID = 1L;

	public StackOverflowException() {
		super("stack overflow");
	}
}
