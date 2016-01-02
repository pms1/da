package com.github.da.t;

import javax.inject.Inject;

public class TM3 implements TypeMapper {

	@Inject
	TM3Config config;

	@Override
	public String toString() {
		return super.toString() + "(config=" + config + ")";
	}
}
