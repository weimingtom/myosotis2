package com.iteye.weimingtom.myosotis.stackbasic;

/**
 * op   : 1 byte
 * arg1 : 4 bytes
 */
public class VMCode {
	public static final int VM_PUSHCONST = 0;
	public static final int VM_PUSHVALUE = 1;
	public static final int VM_POPVALUE = 2;
	public static final int VM_POP = 3;
	public static final int VM_NEG = 4;
	public static final int VM_EQ = 5;
	public static final int VM_NE = 6;
	public static final int VM_GT = 7;
	public static final int VM_GE = 8;
	public static final int VM_LT = 9;
	public static final int VM_LE = 10;
	public static final int VM_ADD = 11;
	public static final int VM_SUB = 12;
	public static final int VM_MUL = 13;
	public static final int VM_DIV = 14;
	public static final int VM_MOD = 15;
	public static final int VM_JMP = 16;
	public static final int VM_JMPC = 17;
	public static final int VM_JMPNC = 18;
	public static final int VM_PRINT = 19;
	public static final int VM_RAND = 20;
	public static final int VM_HALT = 21;
	public static final int VM_MAXCOMMAND = 22; //TODO:
	
	public static final String[] VM_OP_NAMES = {
		"PushConst",
		"PushValue",
		"PopValue",
		"OpPop",
		"OpNeg",
		"OpEq",
		"OpNe",
		"OpGt",
		"OpGe",
		"OpLt",
		"OpLe",
		"OpAdd",
		"OpSub",
		"OpMul",
		"OpDiv",
		"OpMod",
		"OpJmp",
		"OpJmpC",
		"OpJmpNC",
		"OpPrint",
		"OpRand",
		"OpHalt",	
		"LABEL",
	};
	
	private int size;
	private int op;
	private int arg1;
	
	public VMCode(int op) {
		this.size = 1;
		this.op = op;
		this.arg1 = 0;
	}
	
	public VMCode(int op, int arg1) {
		this.size = 5;
		this.op = op;
		this.arg1 = arg1;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getOp() {
		return op;
	}

	public void setOp(int op) {
		this.op = op;
	}

	public int getArg1() {
		return arg1;
	}

	public void setArg1(int arg1) {
		this.arg1 = arg1;
	}
	
	public int Get(byte[] data, int p) {
		//FIXME:
		if (this.op != VMCode.VM_MAXCOMMAND) {
			data[p++] = (byte)this.op;
			if (this.size > 1) {
				data[p++] = (byte)(this.arg1 >>> 0);
				data[p++] = (byte)(this.arg1 >>> 8);
				data[p++] = (byte)(this.arg1 >>> 16);
				data[p++] = (byte)(this.arg1 >>> 24);
			}
		}
		return p;
	}
	
	
}
