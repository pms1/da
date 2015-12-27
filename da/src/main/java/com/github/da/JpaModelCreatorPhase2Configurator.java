package com.github.da;

import java.util.Arrays;
import java.util.Collection;

import com.github.da.JpaClassAnalyser.TResult;

public class JpaModelCreatorPhase2Configurator implements Configurator<NoConfiguration, JpaModelCreatorPhase2> {

	@Override
	public Collection<Object> getRequirements(NoConfiguration config) {
		return Arrays.asList(TResult.class);
	}

	@Override
	public NoConfiguration createConfiguration(Object requirement) {
		if (requirement.equals(JpaProperty.class))
			return NoConfiguration.INSTANCE;

		return null;
	}

	@Override
	public NoConfiguration merge(NoConfiguration config1, NoConfiguration config2) {
		return config1;
	}
}
