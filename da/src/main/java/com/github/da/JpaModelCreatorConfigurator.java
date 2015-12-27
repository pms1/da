package com.github.da;

import java.util.Arrays;
import java.util.Collection;

import com.github.da.JpaClassAnalyser.TResult;
import com.github.pms1.c4.classes.annotations.AnnotationScannerRequirement;

public class JpaModelCreatorConfigurator implements Configurator<JpaModelCreatorConfig, JpaModelCreator> {

	static Collection<Object> reqs = Arrays.asList(ClassHierarchy2.class,
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
}
