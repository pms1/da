package com.github.da.t;

import java.util.List;

import javax.inject.Inject;

import utils.text.Describable;
import utils.text.Description;

@Analysis
public class ClassResourceProcessor implements ResourceProcessor, Describable {

	@Inject
	@All
	List<ClassProcessor> classP;

	@Override
	public void describe(Description d) {
		d.withList("class processors", classP);
	}
}
