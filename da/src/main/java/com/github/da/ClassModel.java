package com.github.da;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;

import asm.ClassType;

public class ClassModel {

	public ClassModel(Type type, ClassType superType, List<Type> interfaces) {
		this.type = type;
		this.superType = superType;
		this.interfaces = interfaces;
	}

	protected final Type type;
	private final ClassType superType;
	protected final List<Type> interfaces;

	public ClassType getSuperType() {
		return superType;
	}

	@Override
	public String toString() {
		return "ClassModel(" + type + ")";
	}

	Map<Class<?>, Object> dataStorage = new HashMap<>();

	<X> void add(Class<X> clazz, X data) {
		Object old = dataStorage.putIfAbsent(clazz, data);
		if (old != null)
			throw new IllegalArgumentException();
	}

	<X> X get(Class<X> clazz) {
		return (X) dataStorage.get(clazz);
	}
}
