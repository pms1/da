package com.github.da;

import java.util.Objects;

public class AnnotationScannerRequirement {
	final String pkg;

	AnnotationScannerRequirement(String pkg) {
		Objects.requireNonNull(pkg);
		if (pkg.isEmpty())
			throw new IllegalArgumentException();
		this.pkg = pkg;
	}
}
