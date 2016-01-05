package com.github.da;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AnalysisConfiguration {

	public AnalysisConfiguration() {
	}

	private final List<AnalyserConfiguration<?>> configs = new LinkedList<>();

	public AnalysisConfiguration with(AnalyserConfiguration<?> config) {
		configs.add(config);
		return this;
	}

	public AnalysisConfiguration withAnalysis(AnalyserConfiguration<?> config) {
		configs.add(config);
		return this;
	}

	public List<AnalyserConfiguration<?>> getAnalyserConfigurations() {
		return new ArrayList<>(configs);
	}
}
