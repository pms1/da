package com.github.da.t;

import com.github.da.ClassData;

public class ClassDataProcessorConfigurator
		implements Configurator<ClassDataProcessor, AnalyserConfiguration<ClassDataProcessor>> {

	@Override
	public AnalyserConfiguration<ClassDataProcessor> createConfiguration(Object requirement) {
		if (requirement == ClassData.class)
			return AnalyserConfiguration.of(ClassDataProcessor.class);
		else
			return null;

	}
}
