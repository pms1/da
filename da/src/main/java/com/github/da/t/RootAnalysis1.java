package com.github.da.t;

import java.util.List;

import javax.inject.Inject;

@Analysis
public class RootAnalysis1 implements RootAnalysis {

	@Inject
	RootAnalysis1Config config;

	@Inject
	@All
	List<ResourceProcessor> all;

	@Override
	public void run() {
		System.err.println("RUN " + config.path + " " + all);
	}

}
