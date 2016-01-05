package com.github.da.jpa;

import java.util.Arrays;
import java.util.Collection;

import com.github.da.ClassHierarchy;
import com.github.pms1.c4.classes.annotations.AnnotationScannerRequirement;

public class JpaModelCreatorConfigurator
		implements com.github.da.t.Configurator<JpaModelCreator, JpaModelCreatorConfig> {

	static Collection<Object> reqs = Arrays.asList(ClassHierarchy.class,
			new AnnotationScannerRequirement("javax.persistence"));

	@Override
	public Collection<Object> getRequirements(JpaModelCreatorConfig config) {
		return reqs;
	}

	@Override
	public JpaModelCreatorConfig createConfiguration(Object requirement) {
		if (requirement.equals(TResult.class))
			return JpaModelCreatorConfig.newBuilder().build();

		return null;
	}

	@Override
	public JpaModelCreatorConfig merge(JpaModelCreatorConfig config1, JpaModelCreatorConfig config2) {
		return config1;
	}
}
