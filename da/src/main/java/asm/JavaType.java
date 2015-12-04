package asm;

public abstract class JavaType {

	public abstract String asJava();

	public abstract <T> T accept(JavaTypeVisitor<T> visitor);
}