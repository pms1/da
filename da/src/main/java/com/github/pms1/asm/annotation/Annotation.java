package com.github.pms1.asm.annotation;

import java.util.Objects;

import org.objectweb.asm.Type;

/**
 * A representation of an annotation.
 * 
 * @author pms1
 *
 */
public class Annotation {
	private final Type type;
	private final AnnotationData data;

	Annotation(Type type, AnnotationData data) {
		Objects.requireNonNull(type);
		this.type = type;
		Objects.requireNonNull(data);
		this.data = data;
	}

	@Override
	public String toString() {
		return "Annotation(" + type + "," + data + ")";
	}

	public AnnotationData getData() {
		return data;
	}
}