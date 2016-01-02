package com.github.da.t;

public class AnalyserConfiguration<A> {
	private final Class<A> analyser;

	public Class<A> getAnalyser() {
		return analyser;
	}

	AnalyserConfiguration(Class<A> c) {
		this.analyser = c;
	}

	public static <A> AnalyserConfiguration<A> of(Class<A> class1) {
		return new AnalyserConfiguration<>(class1);
	}
}
