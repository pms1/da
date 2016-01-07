package com.github.da;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

public abstract class Archive extends GenericData {
	// private final List<ClasspathUnit> children = new LinkedList<>();
	//
	// public <T> Collection<T> getAll(Class<T> class1) {
	// List<T> result = new LinkedList<>();
	//
	// traverse((u) -> {
	// T t = u.find(class1);
	// if (t != null)
	// result.add(t);
	// });
	//
	// return result;
	// }
	//
	// public void add(ClasspathUnit cu) {
	// children.add(cu);
	// }
	//
	// public List<ClasspathUnit> getChildren() {
	// return new ArrayList<>(children);
	// }
	//
	// public void traverse(Consumer<ClasspathUnit> consumer) {
	// consumer.accept(this);
	// for (ClasspathUnit child : children)
	// child.traverse(consumer);
	// }

	final ResourceId id;

	public Archive(ResourceId id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	abstract void add(Archive cu);

	public abstract ClassLoader getClassLoader();

	@Override
	public String toString() {
		return super.toString() + "(id=" + id + ")";
	}

	private static class DataId {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DataId other = (DataId) obj;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (path == null) {
				if (other.path != null)
					return false;
			} else if (!path.equals(other.path))
				return false;
			return true;
		}

		final Path path;
		final Class<?> clazz;

		DataId(Path path, Class<?> clazz) {
			Objects.requireNonNull(path);
			this.path = path;
			Objects.requireNonNull(clazz);
			this.clazz = clazz;
		}

		@Override
		public String toString() {
			return "DataId(" + path + "," + clazz + ")";
		}
	}

	private Map<DataId, Object> data = new HashMap<>();

	public <T> void put(ResourceId id, Class<T> clazz, T value) {
		Objects.requireNonNull(id);
		Preconditions.checkArgument(id.getParent() == this.id);
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(value);

		DataId key = new DataId(id.getPath(), clazz);
		Object existing = data.putIfAbsent(key, value);
		if (existing != null)
			throw new IllegalArgumentException(this + " already contains value of type " + clazz.getCanonicalName());
	}

	public <T> T find(Path path, Class<T> clazz) {
		@SuppressWarnings("unchecked")
		T result = (T) data.get(new DataId(path, clazz));
		return result;
	}

	public <T> Stream<T> findAll(Predicate<Path> p, Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Stream<T> result = (Stream<T>) data.entrySet().stream()
				.filter(e -> p.test(e.getKey().path) && e.getKey().clazz.equals(clazz)).map(Map.Entry::getValue);
		return result;
	}

	public <T> Stream<T> findAll(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Stream<T> result = (Stream<T>) data.entrySet().stream().filter(e -> e.getKey().clazz.equals(clazz))
				.map(Map.Entry::getValue);
		return result;
	}
}
