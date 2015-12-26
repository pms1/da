package com.github.da;

import java.nio.file.Path;

@AnaScope
public class ClasspathElementScannerConfig extends Analysis<ClasspathElementScannerConfig,ClasspathElementScanner> {
	private final Path path;

	public interface Builder {
		Builder withPath(Path p);

		ClasspathElementScannerConfig build();
	}

	private static class BuilderImpl implements Builder {
		Path p;

		@Override
		public ClasspathElementScannerConfig build() {
			return new ClasspathElementScannerConfig(p);
		}

		@Override
		public Builder withPath(Path p) {
			this.p = p;
			return this;
		}

	}

	public ClasspathElementScannerConfig(Path path) {
		super(ClasspathElementScanner.class);
		this.path = path;
	}

	public static Builder newBuilder() {
		return new BuilderImpl();
	}

	public Path getPath() {
		return path;
	}
}
