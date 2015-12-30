package com.github.da.t;

import javax.inject.Inject;

public class A3 {
	@Inject
	A4 a4;

	@Override
	public String toString() {
		return super.toString() + "(a4=" + a4 + ")";
	}
}
