package com.github.da.jpa;

import java.util.Arrays;
import java.util.Collection;

import com.github.da.AnalyserConfiguration;

import asm.ClassSignature;

public class JpaModelCreatorPhase2Configurator
		implements com.github.da.Configurator<JpaModelCreatorPhase2, AnalyserConfiguration<JpaModelCreatorPhase2>> {

	@Override
	public Collection<Object> getRequirements(AnalyserConfiguration<JpaModelCreatorPhase2> config) {
		return Arrays.asList(TResult.class, ClassSignature.class, PersistenceXmlUnits.class);
	}

	@Override
	public AnalyserConfiguration<JpaModelCreatorPhase2> createConfiguration(Object requirement) {
		if (requirement.equals(PersistenceUnits.class))
			return AnalyserConfiguration.of(JpaModelCreatorPhase2.class);

		return null;
	}

	@Override
	public AnalyserConfiguration<JpaModelCreatorPhase2> merge(AnalyserConfiguration<JpaModelCreatorPhase2> config1,
			AnalyserConfiguration<JpaModelCreatorPhase2> config2) {
		return config1;
	}
}
