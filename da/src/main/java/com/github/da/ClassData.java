package com.github.da;

import java.util.HashMap;
import java.util.Map;

public class ClassData extends GenericData {

	private Map<MethodId, MethodData> methodData = new HashMap<MethodId, MethodData>();

	public MethodData get(MethodId classId) {
		MethodData result = methodData.get(classId);
		if (result == null)
			throw new IllegalArgumentException("No data for " + classId);
		return result;
	}

	public void put(MethodId classId, MethodData classModel) {
		MethodData old = methodData.putIfAbsent(classId, classModel);
		if (old != null)
			throw new IllegalArgumentException("Duplicate data for " + classId);
	}

	public void remove(MethodId classId) {
		methodData.remove(classId);
	}

	private Map<FieldId, FieldData> fieldData = new HashMap<FieldId, FieldData>();

	public FieldData get(FieldId classId) {
		FieldData result = fieldData.get(classId);
		if (result == null)
			throw new IllegalArgumentException("No data for " + classId);
		return result;
	}

	public void put(FieldId classId, FieldData classModel) {
		FieldData old = fieldData.putIfAbsent(classId, classModel);
		if (old != null)
			throw new IllegalArgumentException("Duplicate data for " + classId);
	}

	public void remove(FieldId classId) {
		fieldData.remove(classId);
	}
}
