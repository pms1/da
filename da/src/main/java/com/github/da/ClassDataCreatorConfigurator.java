package com.github.da;

import com.github.da.ClassDataCreatorConfig.Data;

public class ClassDataCreatorConfigurator implements Configurator<ClassDataCreatorConfig, ClassDataCreator> {

	@Override
	public ClassDataCreatorConfig createConfiguration(Object r) {
		if (r.equals(ClassHierarchy2.class) || r.equals(ClassData.class)) {
			return new ClassDataCreatorConfig(Data.CLASS);
		} else if (r.equals(MethodData.class)) {
			return new ClassDataCreatorConfig(Data.CLASS, Data.METHOD);
		} else if (r.equals(FieldData.class)) {
			return new ClassDataCreatorConfig(Data.CLASS, Data.FIELD);
		} else {
			return null;
		}
	}
}
