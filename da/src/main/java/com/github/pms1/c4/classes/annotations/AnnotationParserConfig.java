package com.github.pms1.c4.classes.annotations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.da.Configuration;
import com.github.da.t.AnalyserConfiguration;

@Configuration
public class AnnotationParserConfig extends AnalyserConfiguration<AnnotationParser> {
	AnnotationParserConfig(Set<String> packages) {
		super(AnnotationParser.class);
		Objects.requireNonNull(packages);
		if (packages.isEmpty())
			throw new IllegalArgumentException();

		this.packages = Collections.unmodifiableSet(new HashSet<>(packages));
	}

	final Set<String> packages;

	@Override
	public String toString() {
		return "AnnotationParserConfig(packages = " + packages + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((packages == null) ? 0 : packages.hashCode());
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
		AnnotationParserConfig other = (AnnotationParserConfig) obj;
		if (packages == null) {
			if (other.packages != null)
				return false;
		} else if (!packages.equals(other.packages))
			return false;
		return true;
	}
}
