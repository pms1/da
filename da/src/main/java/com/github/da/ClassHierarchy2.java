package com.github.da;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.objectweb.asm.Type;

@AnaScope
public class ClassHierarchy2 {
	private Map<ClassId, ClassData> data = new HashMap<>();

	public ClassData get(ClassId classId) {
		ClassData result = data.get(classId);
		if (result == null)
			throw new IllegalArgumentException("No data for " + classId);
		return result;
	}

	public ClassData find(ClassId classId) {
		return data.get(classId);
	}

	public void put(ClassId classId, ClassData classModel) {
		ClassData old = data.putIfAbsent(classId, classModel);
		if (old != null)
			throw new IllegalArgumentException("Duplicate data for " + classId);
	}

	public void remove(ClassId classId) {
		data.remove(classId);
	}

	public Collection<ClassData> getClasses() {
		return data.values();
	}

	public ClassData get(Type t) {
		Objects.requireNonNull(t);
		return get(AsmIds.forClass(t));
	}

	public ClassData find(Type t) {
		Objects.requireNonNull(t);
		return find(AsmIds.forClass(t));
	}
}
