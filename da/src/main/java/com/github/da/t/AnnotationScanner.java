package com.github.da.t;

import javax.inject.Inject;

import utils.text.Describable;
import utils.text.Description;

public class AnnotationScanner implements ClassProcessor, Describable {

	@Inject
	AnnotationScannerConfig config;

	@Override
	public void describe(Description d) {
		d.withValue("config.what", config.what) //
				.withValue("config.many", config.many);
	}

}
