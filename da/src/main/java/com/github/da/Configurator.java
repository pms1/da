package com.github.da;

import java.util.Collection;
import java.util.Collections;

public interface Configurator<A, C extends AnalyserConfiguration<A>> {

	default Collection<Object> getRequirements(C config) {
		return Collections.emptyList();
	}

	default C createConfiguration(Object requirement) {
		return null;
	}

	default C merge(C config1, C config2) {
		if (config1.getClass().equals(AnalyserConfiguration.class)
				&& config2.getClass().equals(AnalyserConfiguration.class)) {
			if (config1.getAnalyser() != config2.getAnalyser())
				throw new Error();
			return config1;
		} else
			return null;
	}

}
