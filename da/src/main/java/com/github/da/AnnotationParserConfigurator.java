package com.github.da;

import java.util.Arrays;
import java.util.Collection;

public class AnnotationParserConfigurator implements Configurator<AnnotationParserConfig, AnnotationParser> {

	@Override
	public AnnotationParserConfig createConfiguration(Object requirement) {
		if (!(requirement instanceof AnnotationScannerRequirement))
			return null;
		AnnotationScannerRequirement r = (AnnotationScannerRequirement) requirement;

		return new AnnotationParserConfig(Arrays.asList(r.pkg));
	}

	@Override
	public Collection<Object> getRequirements(AnnotationParserConfig config) {
		return Arrays.asList(ClassHierarchy2.class, MethodData.class, ClassData.class, FieldData.class);
	}

}
