package com.github.da.t;

public class AnalyserConfiguration<T> {
	final Class<? extends T> c;

	AnalyserConfiguration(Class<? extends T> c) {
		this.c = c;
	}

	public static <T> AnalyserConfiguration<T> of(Class<T> class1) {
		return new AnalyserConfiguration<T>(class1);
	}
}
