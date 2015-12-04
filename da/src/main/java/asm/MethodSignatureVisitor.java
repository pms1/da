package asm;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

public class MethodSignatureVisitor extends SignatureVisitor {

	// @formatter:off
	// ( visitFormalTypeParameter visitClassBound? visitInterfaceBound* )* (visitParameterType* visitReturnType visitExceptionType* )
	// S1------------------------S2---------------S3-------------------------------------------S4---------------S5-------------------S6
	// @formatter:on

	private enum State {
		S1, S2, S3, S4, S5, S6
	};

	private State state = State.S1;

	public MethodSignatureVisitor() {
		super(Opcodes.ASM5);
	}

	@Override
	public SignatureVisitor visitArrayType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitBaseType(char descriptor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SignatureVisitor visitClassBound() {
		switch (state) {
		case S2:
			state = State.S3;
			break;
		default:
			throw new Error("state=" + state);
		}

		return visitorClassBound = new TypeSignatureVisitor();
	}

	@Override
	public void visitClassType(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitEnd() {
		throw new UnsupportedOperationException();
	}

	private MethodSignature result;

	public MethodSignature getResult() {
		switch (state) {
		case S5:
			result = new MethodSignature(resultParameters,
					visitParameterType.stream().map(p -> p.getResult()).collect(Collectors.toList()),
					visitorReturnType.getResult(),
					visitExceptionType.stream().map(p -> p.getResult()).collect(Collectors.toList()));
			state = State.S6;
			break;
		case S6:
		default:
			throw new Error("state=" + state);
		}

		return result;
	}

	private List<TypeSignatureVisitor> visitExceptionType = new LinkedList<>();

	@Override
	public SignatureVisitor visitExceptionType() {
		switch (state) {
		case S5:
			break;
		default:
			throw new Error("state=" + state);
		}

		TypeSignatureVisitor v = new TypeSignatureVisitor();
		visitExceptionType.add(v);
		return v;
	}

	@Override
	public void visitFormalTypeParameter(String name) {
		switch (state) {
		case S1:
			finishFormalTypeParameter();
			current = new TypeParameter();
			current.name = name;
			state = State.S2;
			break;
		case S3:
			finishClassBound();
			finishInterfaceBound();
			finishFormalTypeParameter();
			current = new TypeParameter();
			current.name = name;
			state = State.S2;
			break;
		default:
			throw new Error();
		}
	}

	@Override
	public void visitInnerClassType(String name) {
		throw new UnsupportedOperationException();

	}

	@Override
	public SignatureVisitor visitInterface() {
		throw new UnsupportedOperationException();

	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		switch (state) {
		case S2:
			finishClassBound();
			state = State.S3;
			break;
		case S3:
			finishClassBound();
			state = State.S3;
			break;
		default:
			throw new Error("state=" + state);
		}

		return visitorInterfaceBound = new TypeSignatureVisitor();
	}

	List<TypeSignatureVisitor> visitParameterType = new LinkedList<>();

	@Override
	public SignatureVisitor visitParameterType() {
		switch (state) {
		case S1:
			state = State.S4;
			break;
		case S2:
			finishFormalTypeParameter();
			state = State.S4;
			break;
		case S3:
			finishClassBound();
			finishInterfaceBound();
			finishFormalTypeParameter();
			state = State.S4;
			break;
		case S4:
			break;
		default:
			throw new Error("state=" + state);
		}

		TypeSignatureVisitor v = new TypeSignatureVisitor();
		visitParameterType.add(v);
		return v;
	}

	TypeSignatureVisitor visitorReturnType;

	@Override
	public SignatureVisitor visitReturnType() {

		switch (state) {
		case S1:
			state = State.S5;
			break;
		case S2:
			finishFormalTypeParameter();
			state = State.S5;
			break;
		case S3:
			finishClassBound();
			finishInterfaceBound();
			finishFormalTypeParameter();
			state = State.S5;
			break;
		case S4:
			state = State.S5;
			break;
		default:
			throw new Error("state=" + state);
		}

		return visitorReturnType = new TypeSignatureVisitor();
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		throw new UnsupportedOperationException();

	}

	@Override
	public void visitTypeArgument() {
		throw new UnsupportedOperationException();

	}

	@Override
	public SignatureVisitor visitTypeArgument(char wildcard) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void visitTypeVariable(String name) {
		throw new UnsupportedOperationException();
	}

	private TypeParameter current;
	private TypeSignatureVisitor visitorClassBound;
	private TypeSignatureVisitor visitorInterfaceBound;

	List<TypeParameter> resultParameters = new LinkedList<>();

	private void finishFormalTypeParameter() {
		if (current == null)
			return;

		resultParameters.add(current);
		current = null;
	}

	private void finishClassBound() {
		if (visitorClassBound == null)
			return;

		if (current == null)
			throw new Error();
		if (current.classBound != null)
			throw new Error();
		current.classBound = visitorClassBound.getResult();

		visitorClassBound = null;
	}

	private void finishInterfaceBound() {
		if (visitorInterfaceBound == null)
			return;
		if (current == null)
			throw new Error();
		current.interfaceBound.add(visitorInterfaceBound.getResult());

		visitorInterfaceBound = null;
	}

}