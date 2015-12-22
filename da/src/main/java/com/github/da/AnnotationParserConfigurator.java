package com.github.da;

import java.util.Arrays;

public class AnnotationParserConfigurator implements Configurator<AnnotationParserConfig, AnnotationParser> {

	@Override
	public AnnotationParserConfig createConfiguration(Object requirement) {
		if (!(requirement instanceof AnnotationScannerRequirement))
			return null;
		AnnotationScannerRequirement r = (AnnotationScannerRequirement) requirement;

		return new AnnotationParserConfig(Arrays.asList(r.pkg));
	}

}
