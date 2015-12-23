package com.github.pms1.c4.classes.annotations;

import java.util.List;
import java.util.Objects;

public class AnnotationParserConfig {
	AnnotationParserConfig(List<String> packages) {
		Objects.requireNonNull(packages);
		if (packages.isEmpty())
			throw new IllegalArgumentException();

		this.packages = packages;
	}

	final List<String> packages;

	@Override
	public String toString() {
		return "AnnotationParserConfig(packages = " + packages + ")";
	}
}