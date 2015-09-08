package com.iteye.weimingtom.myosotis.stackbasic;

public class ValueNode extends Node {
	public ValueNode(String name, Node node) {
		super(Node.OP_VALUE, name, node);
	}
	
	public ValueNode(String name) {
		super(Node.OP_VALUE, name, null);
	}
	
	@Override
	public void push(Compiler c) {
		if (this.getOp() != OP_VALUE) {
			c.error("内部エラー：変数ノードに変数以外が登録されています。");
		} else {
			ValueTag tag = c.GetValueTag(this.getStr());
			if (tag == null) {
				c.error("変数 " + this.getStr() + " は定義されていません。");
			} else {
				c.PushValue(tag.getAddr());
			}
		}
	}

	@Override
	public void pop(Compiler c) {
		if (this.getOp() != OP_VALUE) {
			c.error("内部エラー：変数ノードに変数以外が登録されています。");
		} else {
			ValueTag tag = c.GetValueTag(this.getStr());
			if (tag == null) {
				tag = c.AddValue(this.getStr());
			}
			if (tag == null) {
				c.error("変数 " + this.getStr() + " が定義できません。");
			} else {
				c.PopValue(tag.getAddr());
			}
		}
	}
}
