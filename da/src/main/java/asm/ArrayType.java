package asm;

import java.util.Objects;

public class ArrayType extends JavaType {

	final JavaType contentType;

	public ArrayType(JavaType contentType) {
		Objects.requireNonNull(contentType);
		this.contentType = contentType;
	}

	@Override
	public String asJava() {
		return contentType.asJava() + "[]";
	}

	@Override
	public <T> T accept(JavaTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JavaType getContentType() {
		return contentType;
	}

	@Override
	public String toString() {
		return asJava();
	}

}