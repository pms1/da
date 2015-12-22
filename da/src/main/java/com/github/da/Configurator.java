package com.github.da;

import java.util.Collection;
import java.util.Collections;

public interface Configurator<C, A extends Analyser<C>> {

	default Collection<Object> getRequirements(C config) {
		return Collections.emptyList();
	}

	default C createConfiguration(Object r) {
		return null;
	}

}
