package com.github.da;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AnaScope
public class AnalysisResult {
	final String id;

	AnalysisResult(String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + id + ")";
	}

	private Map<Class<?>, Object> data = new HashMap<>();

	public <T> void put(Class<T> class1, T data1) {
		data.put(class1, data1);

	}

	public <T> T get(Class<T> class1) {
		T result = (T) data.get(class1);
		if (result == null)
			throw new IllegalArgumentException();
		return result;
	}
}
