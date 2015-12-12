package com.github.da;

import java.nio.file.Path;

@AnaScope
public class JarScannerConfig extends Analysis<JarScanner> {
	private final Path path;

	public interface Builder {
		Builder withPath(Path p);

		JarScannerConfig build();
	}

	private static class BuilderImpl implements Builder {
		Path p;

		@Override
		public JarScannerConfig build() {
			return new JarScannerConfig(p);
		}

		@Override
		public Builder withPath(Path p) {
			this.p = p;
			return this;
		}

	}

	public JarScannerConfig(Path path) {
		super(JarScanner.class);
		this.path = path;
	}

	public static Builder newBuilder() {
		return new BuilderImpl();
	}

	public Path getPath() {
		return path;
	}
}
