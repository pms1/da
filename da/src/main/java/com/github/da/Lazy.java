package com.github.da;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {

	private Supplier<T> delegate;
	private T value;

	private Lazy(Supplier<T> delegate) {
		this.delegate = delegate;
	}

	public static <T> Lazy<T> of(Supplier<T> delegate) {
		return new Lazy<>(delegate);
	}

	@Override
	public T get() {
		if (delegate != null) {
			value = delegate.get();
			delegate = null;
		}
		return value;
	}

}