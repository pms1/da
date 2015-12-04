package asm;

import java.util.Objects;

import org.objectweb.asm.Type;

public class RawType extends ClassType {

	private Type type;

	public RawType(Type type) {
		Objects.requireNonNull(type);
		if (type.getSort() != Type.OBJECT)
			throw new IllegalArgumentException();
		this.type = type;
	}

	@Override
	public String asJava() {
		return type.getClassName();
	}

	@Override
	public Type getRawType() {
		return type;
	}

	@Override
	public String toString() {
		return "RawType(" + type.getClassName() + ")";
	}

	@Override
	public <T> T accept(JavaTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
