package asm;

public interface JavaTypeVisitor<T> {

	default T visit(JavaType javaType) {
		throw new UnsupportedOperationException("type = " + javaType);
	}

	default T visit(ClassType classType) {
		return visit((JavaType) classType);
	}

	default T visit(TypeVariable typeVariable) {
		return visit((JavaType) typeVariable);
	}

	default T visit(BaseType baseType) {
		return visit((JavaType) baseType);
	}

	default T visit(ParameterizedType parametrizedType) {
		return visit((ClassType) parametrizedType);
	}

	default T visit(RawType rawType) {
		return visit((ClassType) rawType);
	}

	default T visit(ArrayType arrayType) {
		return visit((JavaType) arrayType);
	}

}
