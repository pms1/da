package com.github.da;

import java.nio.file.Path;

@AnaScope
public class JpaModelCreatorConfig extends Analysis<JpaModelCreator> {

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
