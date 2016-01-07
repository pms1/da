package com.github.da.jpa;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;

public class PersistenceXmlUnit {
	public final String id;

	public final boolean excludeUnlistedClasses;

	public final List<String> classes;

	public final String jtaDataSource;

	public PersistenceXmlUnit(String id, List<String> classes, boolean excludeUnlistedClasses, String jtaDataSource) {
		Objects.requireNonNull(id);
		Preconditions.checkArgument(!id.isEmpty());
		this.id = id;
		Preconditions.checkArgument(!"".equals(jtaDataSource));
		this.jtaDataSource = jtaDataSource;
		this.excludeUnlistedClasses = excludeUnlistedClasses;
		Objects.requireNonNull(classes);
		this.classes = classes;
	}

}
