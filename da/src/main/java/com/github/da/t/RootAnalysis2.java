package com.github.da.t;

import java.util.List;

import javax.inject.Inject;

public class RootAnalysis2 implements RootAnalysis {
	@Inject
	@All
	List<RootAnalysis2a> twoA;

	@Inject
	@Configured
	RootAnalysis2a special;

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
