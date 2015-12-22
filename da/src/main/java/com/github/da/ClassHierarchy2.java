package com.github.da;

import java.util.HashMap;
import java.util.Map;

@AnaScope
public class ClassHierarchy2 {
	private Map<ClassId, ClassData> data = new HashMap<>();

	public ClassData get(ClassId classId) {
		ClassData result = data.get(classId);
		if (result == null)
			throw new IllegalArgumentException("No data for " + classId);
		return result;
	}

	public void put(ClassId classId, ClassData classModel) {
		ClassData old = data.putIfAbsent(classId, classModel);
		if (old != null)
			throw new IllegalArgumentException("Duplicate data for " + classId);
	}

	public void remove(ClassId classId) {
		data.remove(classId);
	}
}
