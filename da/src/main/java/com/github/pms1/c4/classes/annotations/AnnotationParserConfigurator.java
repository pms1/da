package com.github.pms1.c4.classes.annotations;

import java.util.Arrays;
import java.util.Collection;

import com.github.da.ClassData;
import com.github.da.ClassHierarchy2;
import com.github.da.Configurator;
import com.github.da.FieldData;
import com.github.da.MethodData;

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
