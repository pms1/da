package asm;

public class BaseType extends JavaType {

	final char descriptor;

	public BaseType(char descriptor) {
		if (descriptor == 'V')
			throw new IllegalArgumentException();
		this.descriptor = descriptor;
	}

	@Override
	public String asJava() {
		switch (descriptor) {
		case 'B':
			return "byte";
		case 'C':
			return "char";
		case 'D':
			return "double";
		case 'F':
			return "float";
		case 'I':
			return "int";
		case 'J':
			return "long";
		case 'S':
			return "short";
		case 'Z':
			return "boolean";
		default:
			throw new Error(">" + descriptor + "<");
		}
	}

	@Override
	public String toString() {
		return asJava();
	}

	@Override
	public <T> T accept(JavaTypeVisitor<T> visitor) {
		switch (descriptor) {
		case 'B':
			return visitor.visitByte(this);
		case 'C':
			return visitor.visitChar(this);
		case 'D':
			return visitor.visitDouble(this);
		case 'F':
			return visitor.visitFloat(this);
		case 'I':
			return visitor.visitInt(this);
		case 'J':
			return visitor.visitLong(this);
		case 'S':
			return visitor.visitShort(this);
		case 'Z':
			return visitor.visitBoolean(this);
		default:
			throw new Error(">" + descriptor + "<");
		}
	}

}