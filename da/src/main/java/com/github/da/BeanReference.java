package com.github.da;

import java.util.Objects;

public class BeanReference<T> {
	public final Class<T> clazz;

	private BeanReference(Class<T> clazz) {
		Objects.requireNonNull(clazz);
		this.clazz = clazz;
	}

	public static <S> BeanReference<S> forClass(Class<S> clazz2) {
		return new BeanReference<>(clazz2);
	}
}
