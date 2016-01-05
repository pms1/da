package com.github.da;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.da.t.AnalyserConfiguration;
import com.google.common.base.Preconditions;

@Configuration
public class ClassDataCreatorConfig extends AnalyserConfiguration<ClassDataCreator> {
	static enum Data {
		CLASS, METHOD, FIELD, CLASS_TYPE, CLASS_SIGNATURE
	}

	final Set<Data> datas;

	public ClassDataCreatorConfig(Data... datas) {
		super(ClassDataCreator.class);
		Preconditions.checkArgument(datas.length > 0);
		this.datas = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(datas)));
	}

	public ClassDataCreatorConfig(Set<Data> datas) {
		super(ClassDataCreator.class);
		Objects.requireNonNull(datas);
		Preconditions.checkArgument(!datas.isEmpty());
		this.datas = Collections.unmodifiableSet(new HashSet<>(datas));
	}

	@Override
	public String toString() {
		return super.toString() + "(" + datas + ")";
	}
}
