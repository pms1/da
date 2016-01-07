package com.github.da;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
	private Supplier<T> supplier;
	private T value;

	private Lazy(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<T>(supplier);
	}

	@Override
	public T get() {
		if (supplier != null) {
			value = supplier.get();
			supplier = null;
		}

		return value;
	}

}
