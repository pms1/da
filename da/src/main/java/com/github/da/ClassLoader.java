package com.github.da;

import java.nio.file.Path;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.objectweb.asm.Type;

public abstract interface ClassLoader {

	<T> Collection<T> getAll(Path path, Class<T> class1);

	Collection<ClassData> getClasses();

	default ClassData get(Type type) {
		ClassData value = find(type);
		if (value == null)
			throw new NoSuchElementException("Class not found: " + type + " in " + this);
		return value;
	}

	ClassData find(Type rawType);

}
