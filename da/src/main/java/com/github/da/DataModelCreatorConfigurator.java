package com.github.da;

import java.util.Arrays;
import java.util.Collection;

import com.github.da.jpa.PersistenceUnits;
import com.github.da.jpa.TResult;

import asm.ClassSignature;

public class DataModelCreatorConfigurator
		implements com.github.da.Configurator<DataModelCreator, DataModelCreatorConfig> {
	@Override
	public Collection<Object> getRequirements(DataModelCreatorConfig config) {
		return Arrays.asList(PersistenceUnits.class, TResult.class, ClassDataCreatorConfig.Data.CLASS_TYPE,
				ClassSignature.class);
	}
}
