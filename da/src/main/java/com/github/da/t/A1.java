package com.github.da.t;

import java.util.List;

import javax.inject.Inject;

public class A1 implements A {
	@Inject
	C1 c1;

	@Inject
	C2 c2;

	@Inject
	List<A2> a2;

	@Override
	public String toString() {
		return super.toString() + "(a2=" + a2 + ")";
	}
}
