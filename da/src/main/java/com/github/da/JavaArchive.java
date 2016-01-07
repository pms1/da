package com.github.da;

import java.util.Collection;
import java.util.Collections;

import org.objectweb.asm.Type;

public class JavaArchive extends Archive {

	public JavaArchive(ResourceId id) {
		super(id);
	}

	@Override
	void add(Archive cu) {
		System.err.println(this + ":ignoring nested jar " + cu);
	}

	@Override
	public ClassLoader getClassLoader() {
		ClassHierarchy ch = get(ClassHierarchy.class);
		return new ClassLoader() {

			@Override
			public <T> Collection<T> getAll(Class<T> class1) {
				T t = JavaArchive.this.find(class1);
				if (t != null)
					return Collections.singleton(t);
				else
					return Collections.emptyList();
			}

			@Override
			public Collection<ClassData> getClasses() {
				return ch.getClasses();
			}

			@Override
			public ClassData get(Type type) {
				return ch.get(type);
			}

			@Override
			public ClassData find(Type type) {
				return ch.find(type);
			}

		};
	}

}
