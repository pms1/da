package com.github.da;

import java.util.Arrays;
import java.util.Collection;

import asm.ClassSignature;

public class DataModelCreatorConfigurator implements Configurator<DataModelCreatorConfig, DataModelCreator2> {
	@Override
	public Collection<Object> getRequirements(DataModelCreatorConfig config) {
		return Arrays.asList(JpaProperty.class, ClassDataCreatorConfig.Data.CLASS_TYPE, ClassSignature.class);
	}
}
