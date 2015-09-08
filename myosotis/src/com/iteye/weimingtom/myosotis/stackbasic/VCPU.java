package com.iteye.weimingtom.myosotis.stackbasic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VCPU {
	public static final int STACK_SIZE = 1000;
	private Random rand = new Random();
	private VMData data;
	private byte[] command;
	private int command_ptr;
	private int command_size;

	private boolean active;
	
	private VMStack stack = new VMStack();
	private List<Integer> global_value = new ArrayList<Integer>();
	
	public VCPU(VMData data) {
		this.data = data;
	}
	
	/**
	 * Get arguments from bytes.
	 * @see VMCode
	 */
	public int value() { 
		int ret = 0;
		ret  = (this.command[this.command_ptr++] & 0xff) << 0;
		ret |= (this.command[this.command_ptr++] & 0xff) << 8;
		ret |= (this.command[this.command_ptr++] & 0xff) << 16;
		ret |= (this.command[this.command_ptr++] & 0xff) << 24;
		return ret;
	}
	
	public int addr() { 
		return this.command_ptr; 
	}
	
	public void jmp(int addr) { 
		this.command_ptr = addr; 
	}
	
	public void push(int v) throws StackOverflowException { 
		stack.push(v); 
	}
	
	public void pop() { 
		stack.pop(); 
	}
	
	public int top() { 
		return stack.top(); 
	}
	
	public void setTop(int top) {
		stack.setTop(top);
	}
	
	//--------------------------------------
	
	private void PushConst() throws StackOverflowException {
		push(value());
	}

	private void PushValue() throws StackOverflowException {
		push((Integer)global_value.get(value()));
	}

	private void PopValue() {
		global_value.set(value(), (Integer)top()); 
		pop();
	}

	public void OpPop() {
		pop();
	}

	private void OpNeg() {
		setTop(-top());
	}

	// ==
	private void OpEq() throws StackOverflowException {
		int rhs = top(); pop();
		int lhs = top(); pop();
		push((lhs == rhs) ? 1 : 0);
	}

	// !=
	private void OpNe() throws StackOverflowException {
		int rhs = top(); pop();
		int lhs = top(); pop();
		push((lhs != rhs) ? 1 : 0);
	}

	// >
	private void OpGt() throws StackOverflowException {
		int rhs = top(); pop();
		int lhs = top(); pop();
		push((lhs > rhs) ? 1 : 0);
	}

	// >=
	private void OpGe() throws StackOverflowException {
		int rhs = top(); pop();
		int lhs = top(); pop();
		push((lhs >= rhs) ? 1 : 0);
	}
	
	// <
	private void OpLt() throws StackOverflowException {
		int rhs = top(); pop();
		int lhs = top(); pop();
		push((lhs < rhs) ? 1 : 0);
	}

	// <=
	private void OpLe() throws StackOverflowException {
		int rhs = top(); pop();
		int lhs = top(); pop();
		push((lhs <= rhs) ? 1 : 0);
	}

	// +
	private void OpAdd() throws StackOverflowException {
		int rhs = top(); pop();
		int lhs = top(); pop();
		push(lhs + rhs);
	}

	// -
	private void OpSub() throws StackOverflowException {
		int rhs = top(); pop();
		int lhs = top(); pop();
		push(lhs - rhs);
	}

	// *
	private void OpMul() throws StackOverflowException {
		int rhs = top(); pop();
		int lhs = top(); pop();
		push(lhs * rhs);
	}

	// /
	private void OpDiv() throws DevideByZeroException, StackOverflowException {
		int rhs = top(); pop();
		if (rhs == 0) {
			throw new DevideByZeroException();
		}
		int lhs = top(); pop();
		push(lhs / rhs);
	}

	// %
	private void OpMod() throws DevideByZeroException, StackOverflowException {
		int rhs = top(); pop();
		if (rhs == 0) {
			throw new DevideByZeroException();
		}
		int lhs = top(); pop();
		push(lhs / rhs);
	}

	private void OpJmp() {
		jmp(value());
	}

	private void OpJmpC() {
		int addr = value();
		int cond = top(); pop();
		if (cond != 0) {
			jmp(addr);
		}
	}

	private void OpJmpNC() {
		int addr = value();
		int cond = top(); pop();
		if (cond == 0) {
			jmp(addr);
		}
	}

	private void OpHalt() {
		active = false;
	}

	private int rand() {
		/**
		 * FIXME:
		 * don't use nextInt(), use nextInt(maxValue), it should be positive
		 */
		return rand.nextInt(2 << 16);
	}
	
	private void OpRand() throws StackOverflowException {
		int range = top(); pop();
		int value = (range <= 0)? 0: (rand() % range);
		push(value);
	}

	private void OpPrint() {
		int count = value();
		while (count > 0) {
			count--;
			System.out.print(top());
			pop();
			if (count != 0) {
				System.out.print(", ");
			}
		}
		System.out.println();
	}
	
	//-----------------------------------
	/**
	 * @see VMCode
	 * @return
	 */
	public int run() {
		this.command = this.data.command;
		this.command_size = this.data.command_size;
		
		//FIXME:???
		//global_value.resize(this.data.value_size);
		global_value.clear();
		for (int i = 0; i < this.data.value_size; i++) {
			global_value.add(0);
		}
		//
		
		this.command_ptr = 0;
		this.active = true;
		try {
			while (this.active) {
				if (Compiler.DEBUG) {
					System.out.println(">>>>>>>this.command_ptr == " + this.command_ptr);
				}
				int op = this.command[this.command_ptr++];
				int v[];
				switch (op) {
				case VMCode.VM_PUSHCONST:
					PushConst();
					break;
					
				case VMCode.VM_PUSHVALUE:
					PushValue();
					break;

				case VMCode.VM_POPVALUE:
					PopValue();
					break;
					
				case VMCode.VM_POP:
					OpPop();
					break;
					
				case VMCode.VM_NEG:
					OpNeg();
					break;

				case VMCode.VM_EQ:
					OpEq();
					break;

				case VMCode.VM_NE:
					OpNe();
					break;
					
				case VMCode.VM_GT:
					OpGt();
					break;
					
				case VMCode.VM_GE:
					OpGe();
					break;
					
				case VMCode.VM_LT:
					OpLt();
					break;
					
				case VMCode.VM_LE:
					OpLe();
					break;
					
				case VMCode.VM_ADD:
					OpAdd();
					break;
					
				case VMCode.VM_SUB:
					OpSub();
					break;
					
				case VMCode.VM_MUL:
					OpMul();
					break;
					
				case VMCode.VM_DIV:
					OpDiv();
					break;
					
				case VMCode.VM_MOD:
					OpMod();
					break;
	
				case VMCode.VM_JMP:
					OpJmp();
					break;			

				case VMCode.VM_JMPC:
					OpJmpC();
					break;
					
				case VMCode.VM_JMPNC:
					OpJmpNC();
					break;
					
				case VMCode.VM_PRINT:
					OpPrint();
					break;
					
				case VMCode.VM_RAND:
					OpRand();
					break;
					
				case VMCode.VM_HALT:
					OpHalt();
					break;
					
				default:
					break;
				}
			}
		} catch (StackOverflowException e) {
			e.printStackTrace();
			return -1;
		} catch (DevideByZeroException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
