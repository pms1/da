package com.github.da;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.objectweb.asm.Type;

import com.github.da.Archive.DataId;
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
		archive.data.entrySet().stream().filter(e -> e.getKey().path.equals(p) && e.getKey().clazz.equals(class1))
				.forEach(e -> {
					result.add((T) e.getValue());
				});

		return result;
	}

	@Override
	public Collection<ClassData> getClasses() {
		List<ClassData> result = new LinkedList<>();

		archive.data.entrySet().stream().filter(e -> e.getKey().clazz.equals(ClassData.class)).forEach(e -> {
			// FIXME: check path
			result.add((ClassData) e.getValue());
		});

		return result;
	}

	@Override
	public ClassData get(Type type) {
		ClassData value = find(type);
		if (value == null)
			throw new NoSuchElementException("Class not found: " + type);
		return value;
	}

	@Override
	public ClassData find(Type type) {
		Objects.requireNonNull(type);
		Preconditions.checkArgument(type.getSort() == Type.OBJECT);

		DataId id = new DataId(Paths.get(type.getClassName().replace('.', '/') + ".class"), ClassData.class);
		return (ClassData) archive.data.get(id);
	}
}