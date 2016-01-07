package com.github.da;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.objectweb.asm.Type;

import com.google.common.base.Preconditions;

class DefaultArchiveClassLoader implements ClassLoader {
	final Archive archive;

	public DefaultArchiveClassLoader(Archive archive) {
		Objects.requireNonNull(archive);
		this.archive = archive;
	}

	@Override
	public <T> Collection<T> getAll(Path p, Class<T> class1) {
		List<T> result = new LinkedList<>();

		archive.findAll(Predicate.isEqual(p), class1).forEach(result::add);

		return result;
	}

	@Override
	public Collection<ClassData> getClasses() {
		List<ClassData> result = new LinkedList<>();

		archive.findAll(ClassData.class).forEach(e -> {
			// FIXME: check path
			result.add(e);
		});

		return result;
	}

	@Override
	public ClassData find(Type type) {
		Objects.requireNonNull(type);
		Preconditions.checkArgument(type.getSort() == Type.OBJECT);

		return (ClassData) archive.find(Paths.get(type.getClassName().replace('.', '/') + ".class"), ClassData.class);
	}

	@Override
	public String toString() {
		return "DefaultArchiveClassLoader(" + archive + ")";
	}
}