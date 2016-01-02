package com.github.da.t;

import java.nio.file.Path;

import com.github.da.Configuration;

@Configuration
public class RootAnalysis1Config extends AnalyserConfiguration<RootAnalysis1> {

	public RootAnalysis1Config(Path path) {
		super(RootAnalysis1.class);
		this.path = path;
	}

	final Path path;
}
