package com.github.da.t;

import java.util.List;

import javax.inject.Inject;

public class A1 {
	@Inject
	C1 c1;

	@Inject
	C2 c2;

	@Inject
	@Configured
	List<A2> a2;

	@Inject
	@Configured
	List<A2> a21;

	@Override
	public String toString() {
		return super.toString() + "(a2=" + a2 + ")";
	}
}
