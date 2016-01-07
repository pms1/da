package com.github.da.jpa;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.github.da.ClassData;

public class PersistenceUnit {
	public final PersistenceXmlUnit id;

	public final Map<ClassData, JpaAnalysisResult2> data;

	/* package */ PersistenceUnit(PersistenceXmlUnit id, Map<ClassData, JpaAnalysisResult2> data) {
		Objects.requireNonNull(id);
		this.id = id;
		Objects.requireNonNull(data);
		this.data = Collections.unmodifiableMap(data);
	}
}
