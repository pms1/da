package com.github.da;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;

public abstract class GenericData {
	private Map<Class<?>, Object> data = new HashMap<>();

	protected <T> void put(Class<T> class1, Supplier<T> t) {
		Preconditions.checkArgument(class1 != Supplier.class);

		Object old = data.putIfAbsent(class1, t);
		if (old != null)
			throw new IllegalArgumentException("Already entry for '" + class1 + "' in '" + this + "'");
	}

	public <T> void put(Class<T> class1, T data1) {
		Preconditions.checkArgument(class1 != Supplier.class);

		Object old = data.putIfAbsent(class1, data1);
		if (old != null)
			throw new IllegalArgumentException("Already entry for '" + class1 + "' in '" + this + "'");
	}

	public <T> T find(Class<T> class1) {
		return class1.cast(data.get(class1));
	}

	public <T> T get(Class<T> class1) {
		T result = class1.cast(data.get(class1));
		if (result == null)
			throw new IllegalArgumentException("No entry for '" + class1 + "' in '" + this + "'");
		return result;
	}

	public <T> T getOrCreate(Class<T> class1, Supplier<T> s) {
		data.putIfAbsent(class1, s.get());
		T result = class1.cast(data.get(class1));
		assert result != null;
		return result;
	}
}
