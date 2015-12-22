package com.github.da;

import java.util.HashMap;
import java.util.Map;

@AnaScope
public class ClassHierarchy2 {
	private Map<ClassId, ClassModel2> data = new HashMap<>();

	public ClassModel2 get(ClassId classId) {
		ClassModel2 result = data.get(classId);
		if (result == null)
			throw new IllegalArgumentException("No data for " + classId);
		return result;
	}

	public void put(ClassId classId, ClassModel2 classModel) {
		ClassModel2 old = data.putIfAbsent(classId, classModel);
		if (old != null)
			throw new IllegalArgumentException("Duplicate data for " + classId);
	}

	public void remove(ClassId classId) {
		data.remove(classId);
	}
}
