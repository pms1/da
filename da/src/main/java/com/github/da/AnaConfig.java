package com.github.da;

import java.util.LinkedList;
import java.util.List;

public class AnaConfig {
	static class Ana {
		Class<? extends Analyser> clazz;
		Object config;
	}

	List<Ana> analyses = new LinkedList<>();

	<T extends Analyser> AnaConfig withAnalysis(Class<T> t) {
		Ana a = new Ana();
		a.clazz = t;
		analyses.add(a);
		return this;
	}

	<T extends Analyser> AnaConfig withAnalysis(Class<T> t, Object config) {
		Ana a = new Ana();
		a.clazz = t;
		a.config = config;
		analyses.add(a);
		return this;
	}
}
