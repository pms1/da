package com.github.pms1.c4.classes.annotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.github.da.ClassData;
import com.github.da.ClassHierarchy;
import com.github.da.FieldData;
import com.github.da.MethodData;
import com.google.common.collect.Sets;

public class AnnotationParserConfigurator
		implements com.github.da.t.Configurator<AnnotationParser, AnnotationParserConfig> {

	@Override
	public AnnotationParserConfig createConfiguration(Object requirement) {
		if (!(requirement instanceof AnnotationScannerRequirement))
			return null;
		AnnotationScannerRequirement r = (AnnotationScannerRequirement) requirement;

		return new AnnotationParserConfig(Collections.singleton(r.pkg));
	}

	@Override
	public Collection<Object> getRequirements(AnnotationParserConfig config) {
		return Arrays.asList(ClassHierarchy.class, MethodData.class, ClassData.class, FieldData.class);
	}

	@Override
	public AnnotationParserConfig merge(AnnotationParserConfig config1, AnnotationParserConfig config2) {
		return new AnnotationParserConfig(Sets.union(config1.packages, config2.packages));
	}
}
