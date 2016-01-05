package com.github.da;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.Vetoed;

import com.github.da.t.AnalyserConfiguration;

@Vetoed
public class AnalysisConfiguration {
	public String[] what;

	List<Analysis<?, ? extends Analyser<?>>> analyses = new LinkedList<>();

	public <T extends Analyser<? extends Analyser<?>>> AnalysisConfiguration withAnalysis(
			Analysis<?, ? extends Analyser<?>> analysis) {
		analyses.add(analysis);
		return this;
	}

	public <T> AnalysisConfiguration withAnalysis(AnalyserConfiguration<T> build) {
		throw new Error();
	}
}
