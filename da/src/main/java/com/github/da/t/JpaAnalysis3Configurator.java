package com.github.da.t;

import java.util.Arrays;
import java.util.Collection;

import com.github.da.AnalyserConfiguration;
import com.github.da.Configurator;

public class JpaAnalysis3Configurator implements Configurator<JpaAnalysis3, AnalyserConfiguration<JpaAnalysis3>> {

	@Override
	public Collection<Object> getRequirements(AnalyserConfiguration<JpaAnalysis3> config) {
		return Arrays.asList(DummyRequirement3.class);
	}
}
