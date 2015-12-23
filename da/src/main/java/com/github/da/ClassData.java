package com.github.da;

import java.util.HashMap;
import java.util.Map;

public class ClassData extends GenericData {

	private Map<MethodId, MethodData> methodData = new HashMap<MethodId, MethodData>();

	public MethodData get(MethodId methodId) {
		MethodData result = methodData.get(methodId);
		if (result == null)
			throw new IllegalArgumentException("No data for " + methodId);
		return result;
	}

	public void put(MethodId methodId, MethodData data) {
		MethodData old = methodData.putIfAbsent(methodId, data);
		if (old != null)
			throw new IllegalArgumentException("Duplicate data for " + methodId);
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
}
