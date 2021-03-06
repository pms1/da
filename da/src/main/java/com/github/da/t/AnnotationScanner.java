package com.github.da.t;

import javax.inject.Inject;

import org.objectweb.asm.ClassReader;

import com.github.da.ClassProcessor;
import com.github.da.ResourceId;
import com.github.da.Archive;

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

	@Override
	public void run(Archive archive, ResourceId id, ClassReader classReader) {
		System.err.println("DO " + this + " " + config + " " + classReader);
	}

}
