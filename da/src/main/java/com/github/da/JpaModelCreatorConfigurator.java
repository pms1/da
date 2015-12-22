package com.github.da;

import java.util.Arrays;
import java.util.Collection;

public class JpaModelCreatorConfigurator implements Configurator<JpaModelCreatorConfig, JpaModelCreator> {

	static Collection<Object> reqs = Arrays.asList(ClassHierarchy2.class,
			new AnnotationScannerRequirement("javax.persistence"));

	@Override
	public Collection<Object> getRequirements(JpaModelCreatorConfig config) {
		return reqs;
	}
}
