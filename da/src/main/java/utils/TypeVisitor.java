package utils;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

public interface TypeVisitor<R> {
	default R visit(Type type) {
		throw new UnsupportedOperationException("type=" + type);
	}

	default <D extends GenericDeclaration> R visit(TypeVariable<D> v) {
		return visit((Type) v);
	}

	default R visit(WildcardType v) {
		return visit((Type) v);
	}

	default <T> R visit(Class<T> v) {
		return visit((Type) v);
	}

	default <T> R visit(ParameterizedType v) {
		return visit((Type) v);
	}

	static <R> R accept(Type t, TypeVisitor<R> v) {
		Objects.requireNonNull(t);
		Objects.requireNonNull(v);

		int num = 0;

		if (t instanceof WildcardType) {
			return v.visit((WildcardType) t);
		}
		if (t instanceof TypeVariable) {
			return v.visit((TypeVariable<?>) t);
		}
		if (t instanceof Class) {
			return v.visit((Class<?>) t);
		}
		if (t instanceof ParameterizedType) {
			return v.visit((ParameterizedType) t);
		}
		throw new UnsupportedOperationException("T=" + t + " " + t.getClass() + " " + num);
	}
}