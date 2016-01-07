package com.github.da.jpa;

import java.util.List;

public class PersistenceXmlUnit {

	public final boolean excludeUnlistedClasses;

	public final List<String> classes;

	public PersistenceXmlUnit(List<String> classes, boolean excludeUnlistedClasses) {
		this.excludeUnlistedClasses = excludeUnlistedClasses;
		this.classes = classes;
	}

}
