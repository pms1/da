package com.github.da.jpa;

import com.github.da.AnalyserConfiguration;
import com.github.da.Configuration;

@Configuration
public class JpaModelCreatorConfig extends AnalyserConfiguration<JpaModelCreator> {

	public interface Builder {

		JpaModelCreatorConfig build();
	}

	private static class BuilderImpl implements Builder {

		@Override
		public JpaModelCreatorConfig build() {
			return new JpaModelCreatorConfig();
		}

	}

	private JpaModelCreatorConfig() {
		super(JpaModelCreator.class);
	}

	public static Builder newBuilder() {
		return new BuilderImpl();
	}

}
