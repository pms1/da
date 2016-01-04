package com.github.da.t;

import com.github.da.Configuration;

@Configuration
public class RootAnalysis2Config extends AnalyserConfiguration<RootAnalysis2> {

	public RootAnalysis2Config() {
		super(RootAnalysis2.class);
	}

	RootAnalysis2aConfig config = new RootAnalysis2aConfig();
}
