package com.github.da;

import java.nio.file.Path;
import java.util.Collection;

import org.objectweb.asm.Type;

public abstract interface ClassLoader {

	<T> Collection<T> getAll(Path path, Class<T> class1);

	Collection<ClassData> getClasses();

	ClassData get(Type type);

	ClassData find(Type rawType);

}
