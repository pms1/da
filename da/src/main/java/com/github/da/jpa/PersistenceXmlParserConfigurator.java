package com.github.da.jpa;

import com.github.da.AnalyserConfiguration;
import com.github.da.Configurator;

public class PersistenceXmlParserConfigurator
		implements Configurator<PersistenceXmlParser, AnalyserConfiguration<PersistenceXmlParser>> {

	@Override
	public AnalyserConfiguration<PersistenceXmlParser> createConfiguration(Object requirement) {
		if (requirement == PersistenceXmlUnits.class)
			return AnalyserConfiguration.of(PersistenceXmlParser.class);
		else
			return null;
	}
}
