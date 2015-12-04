package asm;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;

public class TypeSignatureVisitor extends SignatureVisitor {

	// @formatter:off
	// visitBaseType | visitTypeVariable | visitArrayType | ( visitClassType visitTypeArgument* (visitInnerClassType visitTypeArgument* )* visitEnd ) )
	// S1-----------S5-----------------S6----------------S7------------------S2-----------------------------------S3------------------------------S4---S8
	// @formatter:on

	private enum State {
		S1, S2, S3, S4, S5, S6, S7, S8
	}

	private TypeSignatureVisitor.State state = State.S1;

	public TypeSignatureVisitor() {
		super(Opcodes.ASM4);
	}

	private JavaType resultArrayTypeContent;

	private TypeSignatureVisitor visitorArrayType;

	@Override
	public SignatureVisitor visitArrayType() {
		switch (state) {
		case S1:
			state = State.S7;
			break;
		default:
			throw new Error("state=" + state);
		}

		return visitorArrayType = new TypeSignatureVisitor();
	}

	@Override
	public void visitBaseType(char descriptor) {
		switch (state) {
		case S1:
			state = State.S5;
			break;
		default:
			throw new Error("state=" + state);
		}
		if (descriptor != 'V')
			result = new BaseType(descriptor);
	}

	@Override
	public SignatureVisitor visitClassBound() {
		throw new Error("state=" + state);
	}

	private Type resultType;

	@Override
	public void visitClassType(String name) {
		switch (state) {
		case S1:
			state = State.S2;
			resultType = Type.getObjectType(name);
			break;
		default:
			throw new Error("state=" + state);
		}

	}

	@Override
	public SignatureVisitor visitExceptionType() {
		throw new Error("state=" + state);

	}

	@Override
	public void visitFormalTypeParameter(String name) {
		throw new Error("state=" + state);

	}

	@Override
	public void visitInnerClassType(String name) {
		switch (state) {
		case S2:
			currentInnerClass = new InnerClass();
			currentInnerClass.name = Type.getObjectType(name);
			state = State.S3;
			break;
		case S3:
			finishInnerClassType();
			currentInnerClass = new InnerClass();
			currentInnerClass.name = Type.getObjectType(name);
			state = State.S3;
			break;
		default:
			throw new Error("state=" + state);
		}
	}

	private InnerClass currentInnerClass;

	private List<InnerClass> resultInnerClasses = new LinkedList<>();

	private void finishInnerClassType() {
		if (currentInnerClass == null)
			throw new Error();
		resultInnerClasses.add(currentInnerClass);
		currentInnerClass = null;
	}

	@Override
	public SignatureVisitor visitInterface() {
		throw new Error("state=" + state);

	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		throw new Error("state=" + state);

	}

	@Override
	public SignatureVisitor visitParameterType() {
		throw new Error("state=" + state);

	}

	@Override
	public SignatureVisitor visitReturnType() {
		throw new Error("state=" + state);

	}

	@Override
	public SignatureVisitor visitSuperclass() {
		throw new Error("state=" + state);

	}

	List<TypeArgument> resultTypeArguments = new LinkedList<>();

	@Override
	public void visitTypeArgument() {
		switch (state) {
		case S2:
			finishTypeArgument();
			resultTypeArguments.add(new TypeArgument());
			break;
		case S3:
			currentInnerClass.typeArguments.add(new TypeArgument());
			break;
		default:
			throw new Error("state=" + state);
		}
	}

	private TypeSignatureVisitor visitorTypeArgument;

	@Override
	public SignatureVisitor visitTypeArgument(char wildcard) {
		switch (state) {
		case S2:
			finishTypeArgument();
			break;
		case S3:
			finishTypeArgument();
			break;
		default:
			throw new Error("state=" + state);
		}

		if (visitorTypeArgument != null)
			throw new Error();
		if (currentTypeArgument != null)
			throw new Error();
		currentTypeArgument = new TypeArgument();
		currentTypeArgument.wildchar = wildcard;
		if (state == State.S2)
			resultTypeArguments.add(currentTypeArgument);
		else
			currentInnerClass.typeArguments.add(currentTypeArgument);

		return visitorTypeArgument = new TypeSignatureVisitor();
	}

	private TypeArgument currentTypeArgument;

	private void finishTypeArgument() {
		if (visitorTypeArgument == null)
			return;
		if (currentTypeArgument == null)
			throw new Error();
		currentTypeArgument.type = visitorTypeArgument.getResult();
		visitorTypeArgument = null;
		currentTypeArgument = null;
	}

	@Override
	public void visitTypeVariable(String name) {
		switch (state) {
		case S1:
			state = State.S6;
			break;
		default:
			throw new Error("state=" + state);
		}

		result = new TypeVariable();
		((TypeVariable) result).name = name;
	}

	private JavaType result;

	@Override
	public void visitEnd() {
		switch (state) {
		case S2:
			finishTypeArgument();
			state = State.S4;
			break;
		case S3:
			finishTypeArgument();
			finishInnerClassType();
			state = State.S4;
			break;
		default:
			throw new Error("state=" + state);
		}

	}

	private void finishArrayType() {
		if (visitorArrayType == null)
			return;
		resultArrayTypeContent = visitorArrayType.getResult();
		visitorArrayType = null;
	}

	private JavaType finalResult;

	public JavaType getResult() {
		switch (state) {
		case S5:
			finalResult = result;
			state = State.S8;
			break;
		case S6:
			finalResult = result;
			state = State.S8;
			break;
		case S7:
			finishArrayType();
			finalResult = new ArrayType(resultArrayTypeContent);
			state = State.S8;
			break;
		case S4:
			finalResult = new ParameterizedType(resultType, resultTypeArguments, resultInnerClasses);
			state = State.S8;
			break;
		case S8:
			break;
		default:
			throw new Error("state=" + state);
		}

		return finalResult;
	}
}