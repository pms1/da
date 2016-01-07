package com.github.da;

import java.util.Objects;

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
}
