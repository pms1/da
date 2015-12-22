package com.github.da;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

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
		T result = class1.cast(data.get(class1));
		if (result == null)
			throw new IllegalArgumentException("No entry for '" + class1 + "'");
		return result;
	}

	public <T> T getOrCreate(Class<T> class1, Supplier<T> s) {
		data.putIfAbsent(class1, s.get());
		T result = class1.cast(data.get(class1));
		assert result != null;
		return result;
	}

}
