package com.github.da;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Type;

public class EnterpriseArchive extends Archive {

	private static class EnterpriseArchiveClassLoader implements ClassLoader {
		final ClassLoader main;
		final List<ClassLoader> children;

		public EnterpriseArchiveClassLoader(ClassLoader main, List<ClassLoader> children) {
			this.main = main;
			this.children = children;
		}

		@Override
		public <T> Collection<T> getAll(Path path, Class<T> class1) {
			List<T> result = new LinkedList<>();

			children.forEach((c) -> result.addAll(c.getAll(path, class1)));

			return result;
		}

		@Override
		public Collection<ClassData> getClasses() {
			throw new Error();
		}

		@Override
		public ClassData find(Type type) {
			ClassData result = null;

			for (ClassLoader c : children) {
				ClassData r = c.find(type);
				if (r != null)
					if (result == null)
						result = r;
					else
						throw new Error("duplicate " + type);
			}

			return result;
		}

		@Override
		public String toString() {
			return "EnterpriseArchiveClassLoader(" + main + "," + children + ")";
		}
	}

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
		List<ClassLoader> loaders = new LinkedList<>();
		for (Archive a : children) {
			DefaultArchiveClassLoader cl = new DefaultArchiveClassLoader(a);
			loaders.add(cl);
		}

		return new EnterpriseArchiveClassLoader(new DefaultArchiveClassLoader(this), loaders);
	}

}
