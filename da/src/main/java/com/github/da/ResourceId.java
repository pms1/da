package com.github.da;

import java.nio.file.Path;

public class ResourceId {
	private final ResourceId parent;
	private final Path path;

	private ResourceId(ResourceId id, Path p) {
		this.path = p;
		this.parent = id;
	}

	public static ResourceId create(ResourceId id, Path p) {
		return new ResourceId(id, p);
	}

	@Override
	public String toString() {
		if (parent != null)
			return parent.path + "!" + path;
		else
			return path.toString();
	}

	public static ResourceId create(Path file) {
		return new ResourceId(null, file);
	}

	public Path getPath() {
		return path;
	}
}
