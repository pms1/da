package com.github.pms1.c4.classes.annotations;

import java.util.Objects;

public class AnnotationScannerRequirement {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
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
		AnnotationScannerRequirement other = (AnnotationScannerRequirement) obj;
		if (pkg == null) {
			if (other.pkg != null)
				return false;
		} else if (!pkg.equals(other.pkg))
			return false;
		return true;
	}

	final String pkg;

	public AnnotationScannerRequirement(String pkg) {
		Objects.requireNonNull(pkg);
		if (pkg.isEmpty())
			throw new IllegalArgumentException();
		this.pkg = pkg;
	}

}
