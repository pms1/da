package com.github.da;

import java.util.LinkedList;
import java.util.List;

import com.github.da.jpa.TypeMapper;

@Configuration
public class DataModelCreatorConfig extends AnalyserConfiguration<DataModelCreator> {
	private final List<Class<? extends TypeMapper>> typeMappers;
	private boolean aggregateByDataSource;

	public interface Builder {
		Builder withTypeMapper(Class<? extends TypeMapper> typeMapper);

		DataModelCreatorConfig build();

		Builder withAggregateByDataSource(boolean value);
	}

	private static class BuilderImpl implements Builder {
		private List<Class<? extends TypeMapper>> typeMappers = new LinkedList<>();
		private boolean aggregateByDataSource = false;

		@Override
		public Builder withTypeMapper(Class<? extends TypeMapper> typeMapper) {
			this.typeMappers.add(typeMapper);
			return this;
		}

		@Override
		public DataModelCreatorConfig build() {
			return new DataModelCreatorConfig(typeMappers, aggregateByDataSource);
		}

		@Override
		public Builder withAggregateByDataSource(boolean value) {
			this.aggregateByDataSource = value;
			return this;
		}

	}

	public DataModelCreatorConfig(List<Class<? extends TypeMapper>> typeMappers, boolean aggregateByDataSource) {
		super(DataModelCreator.class);
		this.typeMappers = typeMappers;
		this.aggregateByDataSource = aggregateByDataSource;
	}

	public static Builder newBuilder() {
		return new BuilderImpl();
	}

	public List<Class<? extends TypeMapper>> getTypeMappers() {
		return typeMappers;
	}

	public boolean isAggregateByDataSource() {
		return aggregateByDataSource;
	}
}
