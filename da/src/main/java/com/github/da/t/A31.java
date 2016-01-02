package com.github.da.t;

import javax.inject.Inject;

public class A31 {
	@Inject
	@Configured
	A41 a41;

	@Override
	public String toString() {
		return super.toString() + "(a41=" + a41 + ")";
	}
}
