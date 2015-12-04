package asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

public class DumpSignatureVisitor extends SignatureVisitor {

	final String prefix;

	public DumpSignatureVisitor(String prefix) {
		super(Opcodes.ASM4);
		if (prefix.endsWith(" "))
			this.prefix = prefix;
		else
			this.prefix = prefix + " ";
	}

	@Override
	public SignatureVisitor visitArrayType() {
		System.err.println(prefix + "visitArrayType");
		return new DumpSignatureVisitor(prefix + " ");
	}

	@Override
	public void visitBaseType(char descriptor) {
		System.err.println(prefix + "visitBaseType descriptor=" + descriptor);
	}

	@Override
	public SignatureVisitor visitClassBound() {
		System.err.println(prefix + "visitClassBound");
		return new DumpSignatureVisitor(prefix + " ");
	}

	@Override
	public void visitClassType(String name) {
		System.err.println(prefix + "visitClassType name=" + name);
	}

	@Override
	public void visitEnd() {
		System.err.println(prefix + "visitEnd");
	}

	@Override
	public SignatureVisitor visitExceptionType() {
		System.err.println(prefix + "visitExceptionType");
		return new DumpSignatureVisitor(prefix + " ");
	}

	@Override
	public void visitFormalTypeParameter(String name) {
		System.err.println(prefix + "visitFormalTypeParameter name=" + name);
	}

	@Override
	public void visitInnerClassType(String name) {
		System.err.println(prefix + "visitInnerClassType name=" + name);
	}

	@Override
	public SignatureVisitor visitInterface() {
		System.err.println(prefix + "visitInterface");
		return new DumpSignatureVisitor(prefix + " ");
	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		System.err.println(prefix + "visitInterfaceBound");
		return new DumpSignatureVisitor(prefix + " ");
	}

	@Override
	public SignatureVisitor visitParameterType() {
		System.err.println(prefix + "visitParameterType");
		return new DumpSignatureVisitor(prefix + " ");
	}

	@Override
	public SignatureVisitor visitReturnType() {
		System.err.println(prefix + "visitReturnType");
		return new DumpSignatureVisitor(prefix + " ");
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		System.err.println(prefix + "visitSuperclass");
		return new DumpSignatureVisitor(prefix + " ");
	}

	@Override
	public void visitTypeArgument() {
		System.err.println(prefix + "visitTypeArgument");

	}

	@Override
	public SignatureVisitor visitTypeArgument(char wildcard) {
		System.err.println(prefix + "visitTypeArgument wildchar=" + wildcard);
		return new DumpSignatureVisitor(prefix + " ");
	}

	@Override
	public void visitTypeVariable(String name) {
		System.err.println(prefix + "visitTypeVariable name=" + name);
	}
}