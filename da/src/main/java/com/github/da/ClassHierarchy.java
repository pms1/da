package com.github.da;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.Type;

@AnaScope
public class ClassHierarchy {
	public ClassModel get(Type t) {
		return ClassProcessor.classes.get(t);
	}

	public Collection<ClassModel> getClasses() {
		return new ArrayList<>(ClassProcessor.classes.values());
	};
}
