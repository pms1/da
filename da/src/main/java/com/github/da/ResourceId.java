package com.github.da;

import java.nio.file.Path;

public class ResourceId {
	private String p;

	private ResourceId(String string) {
		this.p = string;
	}

	public static ResourceId create(ResourceId id, Path p) {
		return new ResourceId(id.p + "!" + p);
	}

	@Override
	public String toString() {
		return p;
	}

	public static ResourceId create(Path file) {
		return new ResourceId(file.toString());
	}
}
