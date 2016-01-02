package com.github.da.t;

import java.util.Arrays;
import java.util.Collection;

public class JpaAnalysis2Configurator implements Configurator<JpaAnalysis2, AnalyserConfiguration<JpaAnalysis2>> {

	@Override
	public Collection<Object> getRequirements(AnalyserConfiguration<JpaAnalysis2> config) {
		return Arrays.asList(DummyRequirement2.class);
	}
}
