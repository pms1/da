package com.github.da;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

public class ClassDataCreatorConfig {
	static enum Data {
		CLASS, METHOD, FIELD
	}

	final Set<Data> datas;

	public ClassDataCreatorConfig(Data... datas) {
		Preconditions.checkArgument(datas.length > 0);
		this.datas = new HashSet<>(Arrays.asList(datas));
	}
}
