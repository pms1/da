package com.github.da;

import java.util.Arrays;
import java.util.Collection;

import com.github.da.jpa.JpaProperty;

import asm.ClassSignature;

public class DataModelCreatorConfigurator
		implements com.github.da.Configurator<DataModelCreator, DataModelCreatorConfig> {
	@Override
	public Collection<Object> getRequirements(DataModelCreatorConfig config) {
		return Arrays.asList(JpaProperty.class, ClassDataCreatorConfig.Data.CLASS_TYPE, ClassSignature.class);
	}
}
