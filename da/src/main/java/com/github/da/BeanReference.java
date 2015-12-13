package com.github.da;

import java.util.Objects;

public class BeanReference<T> {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeanReference<?> other = (BeanReference<?>) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		return true;
	}

	public final Class<T> clazz;

	private BeanReference(Class<T> clazz) {
		Objects.requireNonNull(clazz);
		this.clazz = clazz;
	}

	public static <S> BeanReference<S> forClass(Class<S> clazz2) {
		return new BeanReference<>(clazz2);
	}
}
