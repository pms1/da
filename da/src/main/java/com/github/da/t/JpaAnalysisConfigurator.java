package com.github.da.t;

import java.util.Arrays;
import java.util.Collection;

import com.github.da.AnalyserConfiguration;
import com.github.da.Configurator;

public class JpaAnalysisConfigurator implements Configurator<JpaAnalysis, AnalyserConfiguration<JpaAnalysis>> {

	@Override
	public Collection<Object> getRequirements(AnalyserConfiguration<JpaAnalysis> config) {
		return Arrays.asList(DummyRequirement1.class);
	}
}
