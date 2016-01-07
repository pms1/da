package com.github.da;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Type;

public class EnterpriseArchive extends Archive {

	public EnterpriseArchive(ResourceId id) {
		super(id);
	}

	List<Archive> children = new LinkedList<>();

	@Override
	void add(Archive cu) {
		children.add(cu);
	}

	@Override
	public ClassLoader getClassLoader() {
		List<ClassHierarchy> chs = new LinkedList<>();

		for (Archive a : children) {
			ClassHierarchy find = a.find(ClassHierarchy.class);
			if (find != null)
				chs.add(find);
		}

		return new ClassLoader() {

			@Override
			public <T> Collection<T> getAll(Path p, Class<T> class1) {
				List<T> result = new LinkedList<>();
				for (Archive c : children) {
					T t = c.find(class1);
					if (t != null)
						result.add(t);
				}
				return result;
			}

			@Override
			public Collection<ClassData> getClasses() {
				throw new Error();
			}

			@Override
			public ClassData get(Type type) {
				ClassData result = find(type);
				if (result == null)
					throw new IllegalArgumentException("No data for " + type);

				return result;
			}

			@Override
			public ClassData find(Type type) {
				ClassData result = null;

				for (ClassHierarchy ch : chs) {
					ClassData r = ch.find(type);
					if (r != null)
						if (result != null)
							throw new Error();
						else
							result = r;
				}

				return result;
			}

		};
	}

}
