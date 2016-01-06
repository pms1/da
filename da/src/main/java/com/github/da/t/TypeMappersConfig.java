package com.github.da.t;

import java.util.LinkedList;
import java.util.List;

import com.github.da.AnalyserConfiguration;
import com.github.da.Configuration;

@Configuration
public class TypeMappersConfig extends AnalyserConfiguration<TypeMappers> {

	public TypeMappersConfig() {
		super(TypeMappers.class);
	}

	public TypeMappersConfig withTypeMapper(AnalyserConfiguration<? extends TypeMapper> config) {
		if (this.configs == null)
			this.configs = new LinkedList<>();
		this.configs.add(config);
		return this;
	}

	List<AnalyserConfiguration<? extends TypeMapper>> configs;
}
