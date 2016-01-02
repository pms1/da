package com.github.da.t;

import javax.inject.Inject;

public class RootAnalysis1 implements RootAnalysis {

	@Inject
	RootAnalysis1Config config;

	// @Inject
	// @All
	// List<ResourceProcessor> all;

	@Override
	public void run() {
		System.err.println("RUN " + config.path);
	}

}
