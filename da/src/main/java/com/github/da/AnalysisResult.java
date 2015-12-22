package com.github.da;

import java.util.Objects;

@AnaScope
public class AnalysisResult extends GenericData {
	final String id;

	AnalysisResult(String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + id + ")";
	}

}
