package com.github.da.t;

public class RootAnalysis2aConfigurator implements Configurator<RootAnalysis2a, AnalyserConfiguration<RootAnalysis2a>> {
	@Override
	public AnalyserConfiguration<RootAnalysis2a> createConfiguration(Object requirement) {
		if (requirement == DeploymentStructure.class)
			return new RootAnalysis2aConfig();
		else
			return null;
	}
}
