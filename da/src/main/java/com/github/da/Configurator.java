package com.github.da;

import java.util.Collection;
import java.util.Collections;

public interface Configurator<C, A extends Analyser<C>> {

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
