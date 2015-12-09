package com.github.da;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class AnalysisConfiguration {
	public String[] what;

	List<Analysis> analyses = new LinkedList<>();

	public <T extends Analyser> AnalysisConfiguration withAnalysis(Analysis analysis) {
		analyses.add(analysis);
		return this;
	}
}
