package com.github.da;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClassData extends GenericData {
	private final ClassId classId;

	ClassData(ClassId classId) {
		Objects.requireNonNull(classId);
		this.classId = classId;
	}

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

	@Override
	public String toString() {
		return super.toString() + "(" + classId + ")";
	}
}
