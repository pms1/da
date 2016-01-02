package com.github.da.t;

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
		return null;
	}

}
