package com.iteye.weimingtom.myosotis.stackbasic;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 * @author Administrator
 * 
 */
public class Compiler {
	public static final boolean DEBUG = false;
	
	private ValueTable variables = new ValueTable();
	//VMCode 
	private List<VMCode> statement = new ArrayList<VMCode>();
	//Label
	private List<Label> labels = new ArrayList<Label>();
	//State
	private Stack<State> state_stack = new Stack<State>();
	private int error_count = 0;
	private String file;
	
	//--------------------------------------------
	
	public void PushConst(int arg1) {
		statement.add(new VMCode(VMCode.VM_PUSHCONST, arg1));
	}
	
	public void PushValue(int arg1) {
		statement.add(new VMCode(VMCode.VM_PUSHVALUE, arg1));
	}
	
	public void PopValue(int arg1) {
		statement.add(new VMCode(VMCode.VM_POPVALUE, arg1));
	}
	
	public void OpPop() {
		statement.add(new VMCode(VMCode.VM_POP));
	}
	
	public void OpNeg() {
		statement.add(new VMCode(VMCode.VM_NEG));
	}
	
	public void OpEq() {
		statement.add(new VMCode(VMCode.VM_EQ));
	}

	public void OpNe() {
		statement.add(new VMCode(VMCode.VM_NE));
	}
	
	public void OpGt() {
		statement.add(new VMCode(VMCode.VM_GT));
	}
	
	public void OpGe() {
		statement.add(new VMCode(VMCode.VM_GE));
	}
	
	public void OpLt() {
		statement.add(new VMCode(VMCode.VM_LT));
	}
	
	public void OpLe() {
		statement.add(new VMCode(VMCode.VM_LE));
	}
		
	public void OpAdd() {
		statement.add(new VMCode(VMCode.VM_ADD));
	}
	
	public void OpSub() {
		statement.add(new VMCode(VMCode.VM_SUB));
	}
	
	public void OpMul() {
		statement.add(new VMCode(VMCode.VM_MUL));
	}
	
	public void OpDiv() {
		statement.add(new VMCode(VMCode.VM_DIV));
	}
	
	public void OpMod() {
		statement.add(new VMCode(VMCode.VM_MOD));
	}
	
	public void OpJmp(int arg1) {
		statement.add(new VMCode(VMCode.VM_JMP, arg1));
	}	
	
	public void OpJmpC(int arg1) {
		statement.add(new VMCode(VMCode.VM_JMPC, arg1));
	}
	
	public void OpJmpNC(int arg1) {
		statement.add(new VMCode(VMCode.VM_JMPNC, arg1));
	}
	
	public void OpPrint(int arg1) {
		statement.add(new VMCode(VMCode.VM_PRINT, arg1));
	}
	
	public void OpRand() {
		statement.add(new VMCode(VMCode.VM_RAND));
	}

	public void OpHalt() {
		statement.add(new VMCode(VMCode.VM_HALT));
	}
	
	//--------------------------------------------
	
	public Compiler() {
		
	}
	
	public boolean compile(String str, VMData data) {
		file = str; 
		//FIXME:
		int result = 0;
		//
		ANTLRStringStream input = new ANTLRStringStream(str);
		StackBasicLexer lexer = new StackBasicLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		StackBasicParser parser = new StackBasicParser(tokens);
		// Share data here 
		parser.driver = this;
		try {
			parser.compilationUnit();
		} catch (RecognitionException e) {
			e.printStackTrace();
			result = -1;
		} finally {
			parser.driver = null;
		}
		if (result != 0) {
			return false;
		}
		while (!state_stack.empty()) {
			State state = state_stack.peek();
			switch (state.getState()) {
			case State.STATE_IF:
				error("if does not match endif");
				break;

			case State.STATE_FOR:
				error("for does not match next");
				state.setStart(null);
				state.setEnd(null);
				state.setStep(null);
				break;

			case State.STATE_WHILE:
				error("while does not match wend");
				break;
			}
			state_stack.pop();
		}
		//FIXME:
		VMCode code = statement.get(statement.size() - 1);
		if (code.getOp() != VMCode.VM_HALT) {
			OpHalt();
		}
		int code_size = LabelSetting();
		//FIXME:CraeteData
		//from statements to data (dump to byte array) 
		CreateData(data, code_size);
		return error_count == 0;
	}
	
	public void error(String m) {
		System.err.println(m);
		error_count++;
	}
	
	public void debug_dump() {
		System.out.println("---variables---");
		variables.dump();
		String[] op_name = VMCode.VM_OP_NAMES;
		System.out.println("---code---");
		int	pos = 0;
		int size = statement.size();
		for (int i = 0; i < size; i++) {
			VMCode code = (VMCode)statement.get(i);
			System.out.print(pos + ": " + op_name[code.getOp()]);
			if (code.getSize() > 1) {
				System.out.print(", " + code.getArg1());
			}
			System.out.println();
			if (code.getOp() != VMCode.VM_MAXCOMMAND) {
				pos += code.getSize();
			}
		}
		System.out.println("---");
	}

	public ValueTag GetValueTag(String name) {
		return variables.find(name);
	}
	
	public ValueTag AddValue(String name) {
		if (variables.add(name, 1)) {
			return variables.find(name);
		}
		return null;
	}
	
	//--------------------------------------------
	
	public int LabelSetting() {
		int pos = 0;
		Iterator<VMCode> it = statement.iterator();
		while(it.hasNext()) {
			VMCode code = it.next();
			if (code.getOp() == VMCode.VM_MAXCOMMAND) {
				labels.get(code.getArg1()).setPos(pos);
			} else {
				pos += code.getSize();
			}
		}
		Iterator<VMCode> it2 = statement.iterator();
		while(it2.hasNext()) {
			VMCode code = it2.next();
			switch (code.getOp()) {
			case VMCode.VM_JMP:
			case VMCode.VM_JMPC:
			case VMCode.VM_JMPNC:
				code.setArg1(labels.get(code.getArg1()).getPos());
				break;
			}
		}
		return pos;
	}
	
	public int MakeLabel() {
		int index = labels.size();
		labels.add(new Label(index));
		return index;
	}
	
	public void SetLabel(int label) {
		statement.add(new VMCode(VMCode.VM_MAXCOMMAND, label));
	}
	
	//FIXME:CraeteData
	public boolean CreateData(VMData data, int code_size) {
		data.command = new byte[code_size];
		data.command_size = code_size;
		data.value_size = variables.size();
		Iterator<VMCode> it = statement.iterator();
		int p = 0;
		// NOTE: I forget "while" isn't 'if' !!!
		while (it.hasNext()) {
			VMCode code = it.next();
			p = code.Get(data.command, p);
		}
		return true;
	}
	
	//-------------------------------------
	
	public void AssignStatement(Assign assign) {
		assign.analyze(this);
	}
	
	public void IfStatement(Node expr) {
		expr.push(this);
		int label = MakeLabel();
		OpJmpNC(label);
		state_stack.push(new State(State.STATE_IF, label));
	}
	
	public void ElseStatement() {
		if (state_stack.empty() || 
			state_stack.peek().getState() != State.STATE_IF) {
			error("if doesn't have else");
		} else {
			State state = state_stack.peek();
			int label = MakeLabel();
			OpJmp(label);
			SetLabel(state.getLabel1());
			state.setLabel1(label);
		}
	}
	
	public void EndifStatement() {
		if (state_stack.empty() || 
			state_stack.peek().getState() != State.STATE_IF) {
			error("if doesn't have endif");
		} else {
			State state = state_stack.peek();
			SetLabel(state.getLabel1());
			state_stack.pop();
		}
	}
	
	public void ForStatement(Assign start, Node end, Node step) {
		int label = MakeLabel();
		start.analyze(this);
		SetLabel(label);
		state_stack.push(new State(State.STATE_FOR, label, start, end, step));
	}
	
	public void NextStatement() {
		if (state_stack.empty() || 
			state_stack.peek().getState() != State.STATE_FOR) {
			error("for doesn't have next");
		} else {
			State state = state_stack.peek();
			int label = MakeLabel();
			// ループ終了のチェック
			state.getStart().push_value(this);
			state.getEnd().push(this);
			OpEq();
			OpJmpC(label);			// 終了時飛び先

			// カウンター増分
			state.getStart().push_value(this);
			if (state.getStep() != null) {
				state.getStep().push(this);
			} else {
				PushConst(1);
			}
			OpAdd();
			state.getStart().pop_value(this);

			
			OpJmp(state.getLabel1());
			SetLabel(label);
			state.setStart(null);
			state.setEnd(null);
			state.setStep(null);
			state_stack.pop();
		}
	}
	
	public void WhileStatement(Node expr) {
		int label1 = MakeLabel();
		int label2 = MakeLabel();
		SetLabel(label1);
		expr.push(this);
		OpJmpNC(label2);
		state_stack.push(new State(State.STATE_WHILE, label1, label2));
		expr = null;
	}
	
	public void WendStatement() {
		if (state_stack.empty() || state_stack.peek().getState() != State.STATE_WHILE) {
			error("while does not match wend");
		} else {
			State state = state_stack.peek();
			OpJmp(state.getLabel1());
			SetLabel(state.getLabel2());
			state_stack.pop();
		}
	}
	
	public void EndStatement() {
		OpHalt();
	}
	
	public void PrintStatement(Args args) {
		int arg_count = 0;
		if (args != null) {
			Iterator<Node> it = args.getReverseIterator();
			while (it.hasNext()) {
				Node node = it.next();
				node.push(this);
			}
			arg_count = args.getSize();
		}
		OpPrint(arg_count);
		args = null;
	}
}
