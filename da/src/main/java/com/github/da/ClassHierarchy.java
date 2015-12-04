package com.github.da;

import java.util.Collection;

import org.objectweb.asm.Type;

public interface ClassHierarchy {
	ClassModel get(Type t);

	Collection<ClassModel> getClasses();
}
