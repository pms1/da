package com.github.da;

import com.github.da.ClassDataCreatorConfig.Data;
import com.google.common.collect.Sets;

import asm.ClassSignature;

public class ClassDataCreatorConfigurator
		implements com.github.da.t.Configurator<ClassDataCreator, ClassDataCreatorConfig> {

	@Override
	public ClassDataCreatorConfig createConfiguration(Object r) {
		if (r.equals(ClassHierarchy2.class) || r.equals(ClassData.class)) {
			return new ClassDataCreatorConfig(Data.CLASS);
		} else if (r.equals(MethodData.class)) {
			return new ClassDataCreatorConfig(Data.CLASS, Data.METHOD);
		} else if (r.equals(FieldData.class)) {
			return new ClassDataCreatorConfig(Data.CLASS, Data.FIELD);
		} else if (r == Data.CLASS_TYPE) {
			return new ClassDataCreatorConfig(Data.CLASS, Data.CLASS_TYPE);
		} else if (r.equals(ClassSignature.class)) {
			return new ClassDataCreatorConfig(Data.CLASS, Data.CLASS_SIGNATURE);
		} else {
			return null;
		}
	}

	@Override
	public ClassDataCreatorConfig merge(ClassDataCreatorConfig config1, ClassDataCreatorConfig config2) {
		return new ClassDataCreatorConfig(Sets.union(config1.datas, config2.datas));
	}
}
