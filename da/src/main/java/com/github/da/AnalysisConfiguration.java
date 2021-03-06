package com.github.da;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AnalysisConfiguration {

	public AnalysisConfiguration() {
	}

	private final List<AnalyserConfiguration<?>> configs = new LinkedList<>();
	private final List<Object> requirements = new LinkedList<>();

	public AnalysisConfiguration withAnalysis(AnalyserConfiguration<?> config) {
		configs.add(config);
		return this;
	}

	public AnalysisConfiguration withResult(Object requirement) {
		requirements.add(requirement);
		return this;
	}

	public List<AnalyserConfiguration<?>> getAnalyserConfigurations() {
		return new ArrayList<>(configs);
	}

	public List<Object> getResults() {
		return new ArrayList<>(configs);
	}

}
