package asm;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

public class ClassSignatureVisitor extends SignatureVisitor {

	// @formatter:off
	// ( visitFormalTypeParameter visitClassBound? visitInterfaceBound* )* (visitSuperClass visitInterface* )
	// S1------------------------S2---------------S3----------------------------------------S4---------------S5
	// @formatter:on

	private TypeParameter current;

	private enum State {
		S1, S2, S3, S4, S5
	};

	private State state = State.S1;

	private ClassSignature result;

	public ClassSignatureVisitor() {
		super(Opcodes.ASM4);
	}

	@Override
	public SignatureVisitor visitArrayType() {
		throw new UnsupportedOperationException();
	}

	private TypeSignatureVisitor visitorClassBound;

	public ClassSignature getResult() {
		switch (state) {
		case S4:
			finishSuperclass();
			finishInterface();
			if (current != null)
				throw new Error();
			result = new ClassSignature(resultParameters, resultSuperclass, resultInterfaces);
			state = State.S5;
			break;
		case S5:
			break;
		default:
			throw new Error("State=" + state);
		}

		return result;
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
	public void visitBaseType(char descriptor) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void visitClassType(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SignatureVisitor visitExceptionType() {
		throw new UnsupportedOperationException();
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

	private final List<TypeParameter> resultParameters = new LinkedList<>();

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

	@Override
	public void visitInnerClassType(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SignatureVisitor visitInterface() {
		switch (state) {
		case S4:
			finishSuperclass();
			finishInterface();
			break;
		default:
			throw new Error();
		}

		return visitorInterface = new TypeSignatureVisitor();
	}

	private TypeSignatureVisitor visitorInterfaceBound;

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

	@Override
	public SignatureVisitor visitParameterType() {
		throw new UnsupportedOperationException();

	}

	@Override
	public SignatureVisitor visitReturnType() {
		throw new UnsupportedOperationException();

	}

	TypeSignatureVisitor visitorSuperclass;

	TypeSignatureVisitor visitorInterface;

	private List<ClassType> resultInterfaces = new LinkedList<>();

	void finishInterface() {
		if (visitorInterface == null)
			return;

		resultInterfaces.add((ClassType) visitorInterface.getResult());

		visitorInterface = null;
	}

	private ClassType resultSuperclass;

	void finishSuperclass() {
		if (visitorSuperclass == null)
			return;

		if (resultSuperclass != null)
			throw new Error();
		resultSuperclass = (ClassType) visitorSuperclass.getResult();

		visitorSuperclass = null;
	}

	@Override
	public SignatureVisitor visitSuperclass() {
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
		default:
			throw new Error("state=" + state);
		}

		return visitorSuperclass = new TypeSignatureVisitor();
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

	@Override
	public void visitEnd() {
		throw new UnsupportedOperationException();
	}

}