package com.github.da;

import java.util.Objects;

public class MethodId {
	private final String id;

	public MethodId(String id) {
		Objects.requireNonNull(id);

		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodId other = (MethodId) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "MethodId(" + id + ")";
	}
}
