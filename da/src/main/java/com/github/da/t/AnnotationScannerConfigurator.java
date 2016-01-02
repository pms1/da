package com.github.da.t;

public class AnnotationScannerConfigurator implements Configurator<AnnotationScanner, AnnotationScannerConfig> {

	@Override
	public AnnotationScannerConfig createConfiguration(Object requirement) {
		if (requirement == DummyRequirement1.class)
			return new AnnotationScannerConfig(1, 1);
		if (requirement == DummyRequirement2.class)
			return new AnnotationScannerConfig(2, 1);
		return null;
	}

	@Override
	public AnnotationScannerConfig merge(AnnotationScannerConfig config1, AnnotationScannerConfig config2) {
		if (config1.what == config2.what)
			return new AnnotationScannerConfig(config1.what, config1.many + config2.many);
		else
			return null;
	}
}
