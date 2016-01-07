package com.github.da;

import java.util.Collection;
import java.util.Collections;

import org.objectweb.asm.Type;

public class DirectoryArchive extends Archive {

	@Override
	void add(Archive cu) {
		throw new Error();
	}

	@Override
	public ClassLoader getClassLoader() {
		ClassHierarchy ch = get(ClassHierarchy.class);
		return new ClassLoader() {

			@Override
			public <T> Collection<T> getAll(Class<T> class1) {
				T t = DirectoryArchive.this.find(class1);
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
