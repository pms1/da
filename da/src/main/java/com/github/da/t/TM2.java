package com.github.da.t;

import javax.inject.Inject;

public class TM2 implements TypeMapper {

	@Inject
	TM2Config config;

	@Override
	public String toString() {
		return super.toString() + "(config=" + config + ")";
	}
}
