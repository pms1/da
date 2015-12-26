package com.github.da;

import java.util.LinkedList;
import java.util.List;

@AnaScope
public class DataModelCreatorConfig extends Analysis<DataModelCreatorConfig,DataModelCreator> {
	private final List<Class<? extends TypeMapper>> typeMappers;

	public interface Builder {
		Builder withTypeMapper(Class<? extends TypeMapper> typeMapper);

		DataModelCreatorConfig build();
	}

	private static class BuilderImpl implements Builder {
		List<Class<? extends TypeMapper>> typeMappers = new LinkedList<>();

		@Override
		public Builder withTypeMapper(Class<? extends TypeMapper> typeMapper) {
			this.typeMappers.add(typeMapper);
			return this;
		}

		@Override
		public DataModelCreatorConfig build() {
			return new DataModelCreatorConfig(typeMappers);
		}

	}

	public DataModelCreatorConfig(List<Class<? extends TypeMapper>> typeMappers) {
		super(DataModelCreator.class);
		this.typeMappers = typeMappers;
	}

	public static Builder newBuilder() {
		return new BuilderImpl();
	}

	public List<Class<? extends TypeMapper>> getTypeMappers() {
		return typeMappers;
	}
}
