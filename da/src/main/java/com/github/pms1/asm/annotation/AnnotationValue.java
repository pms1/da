package com.github.pms1.asm.annotation;

import java.util.List;
import java.util.Objects;

import org.objectweb.asm.Type;

/**
 * The value of a single property of an annotation paired with the value's type.
 * In case the value represents an empty array, {@code type} will be
 * {@code null}. The {@code value} field will an contain a {@link List} object
 * for arrays, a {@link Type} object for properties with type {@link Class} and
 * an {@link Annotation} object for a nested annotation.
 * 
 * @author pms1
 */
public class AnnotationValue {
	/* package */ AnnotationValue(Type type, Object value) {
		this.type = type;
		Objects.requireNonNull(value);
		this.value = value;
	}

	final Type type;
	private final Object value;

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		if (type == null)
			return value.toString();
		else
			return "(" + type + ") " + value;
	}
}