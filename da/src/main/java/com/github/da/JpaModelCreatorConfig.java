package com.github.da;

import com.github.da.t.AnalyserConfiguration;

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
