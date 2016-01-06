package com.github.da.jpa;

import java.util.Collections;
import java.util.Map;

import com.github.da.ClassData;

public class PersistenceUnit {
	public final Map<ClassData, JpaAnalysisResult2> data;

	/* package */ PersistenceUnit(Map<ClassData, JpaAnalysisResult2> data) {
		this.data = Collections.unmodifiableMap(data);
	}
}
