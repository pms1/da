package asm;

public class TypeVariable extends JavaType {

	String name;

	@Override
	public String asJava() {
		return name;
	}

	@Override
	public String toString() {
		return "TypeVariable(" + name + ")";
	}

	@Override
	public <T> T accept(JavaTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}