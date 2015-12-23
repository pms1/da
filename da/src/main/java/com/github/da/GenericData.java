package com.github.da;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class GenericData {
	private Map<Class<?>, Object> data = new HashMap<>();

	public <T> void put(Class<T> class1, T data1) {
		Object old = data.putIfAbsent(class1, data1);
		if (old != null)
			throw new IllegalArgumentException("Already entry for '" + class1 + "'");
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