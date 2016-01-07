package com.github.da;

import java.nio.file.Path;

@Configuration
public class ZipScannerConfig extends AnalyserConfiguration<ZipScanner> {
	private final Path path;

	public interface Builder {
		Builder withPath(Path p);

		ZipScannerConfig build();
	}

	private static class BuilderImpl implements Builder {
		Path p;

		@Override
		public ZipScannerConfig build() {
			return new ZipScannerConfig(p);
		}

		@Override
		public Builder withPath(Path p) {
			this.p = p;
			return this;
		}

	}

	public ZipScannerConfig(Path path) {
		super(ZipScanner.class);
		this.path = path;
	}

	public static Builder newBuilder() {
		return new BuilderImpl();
	}

	public Path getPath() {
		return path;
	}
}
