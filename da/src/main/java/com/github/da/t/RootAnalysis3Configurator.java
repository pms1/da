package com.github.da.t;

import java.util.Arrays;
import java.util.Collection;

public class RootAnalysis3Configurator implements Configurator<RootAnalysis3, AnalyserConfiguration<RootAnalysis3>> {
	@Override
	public Collection<Object> getRequirements(AnalyserConfiguration<RootAnalysis3> config) {
		return Arrays.asList(DeploymentStructure.class);
	}

}
