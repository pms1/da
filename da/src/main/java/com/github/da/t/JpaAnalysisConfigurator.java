package com.github.da.t;

import java.util.Arrays;
import java.util.Collection;

public class JpaAnalysisConfigurator implements Configurator<JpaAnalysis, AnalyserConfiguration<JpaAnalysis>> {

	@Override
	public Collection<Object> getRequirements(AnalyserConfiguration<JpaAnalysis> config) {
		return Arrays.asList(DummyRequirement1.class);
	}
}
