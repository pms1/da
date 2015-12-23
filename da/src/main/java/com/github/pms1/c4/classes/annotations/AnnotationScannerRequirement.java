package com.github.pms1.c4.classes.annotations;

import java.util.Objects;

public class AnnotationScannerRequirement {
	final String pkg;

	public AnnotationScannerRequirement(String pkg) {
		Objects.requireNonNull(pkg);
		if (pkg.isEmpty())
			throw new IllegalArgumentException();
		this.pkg = pkg;
	}
}
