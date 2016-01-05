package com.github.da.t;

import java.util.List;

import javax.inject.Inject;

import com.github.da.Analysis;
import com.github.da.ResourceProcessor;

import utils.text.Describable;
import utils.text.Description;

@Analysis
public class RootAnalysis1 implements RootAnalysis, Describable {

	@Inject
	RootAnalysis1Config config;

	@Inject
	@All
	List<ResourceProcessor> all;

	@Override
	public void run() {
		System.err.println("RUN " + config.path + " " + all);
	}

	@Override
	public void describe(Description d) {
		d.withValue("path", config.path)//
				.withList("resource processors", all);
	}
}
