package com.github.pms1.asm.annotation;

import java.util.Collections;
import java.util.Map;

/**
 * A representation of the content of an annotation.
 * 
 * @author pms1
 *
 */
public class AnnotationData {
	private final Map<String, AnnotationValue> data;

	private AnnotationData(Map<String, AnnotationValue> data) {
		this.data = Collections.unmodifiableMap(data);
	}

	public static AnnotationData of(Map<String, AnnotationValue> fin) {
		return new AnnotationData(fin);
	}

	public Map<String, AnnotationValue> getData() {
		return data;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
