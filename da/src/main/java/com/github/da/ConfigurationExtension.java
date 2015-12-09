package com.github.da;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

/**
 * @author pms1
 *
 */
class ConfigurationExtension implements Extension {
	private final AnalysisConfiguration analysisConfiguration;

	ConfigurationExtension(AnalysisConfiguration analysisConfiguration) {
		Objects.requireNonNull(analysisConfiguration);
		this.analysisConfiguration = analysisConfiguration;
	}

	AnalysisConfiguration getAnalysisConfiguration() {
		return analysisConfiguration;
	}

	@ApplicationScoped
	static class ConfigrationFactory {
		@Inject
		ConfigurationExtension e;

		@Produces
		AnalysisConfiguration produceAnalysisConfiguration() {
			return e.getAnalysisConfiguration();
		}
	}

}
