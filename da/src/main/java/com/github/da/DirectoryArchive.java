package com.github.da;

public class DirectoryArchive extends Archive {

	public DirectoryArchive(ResourceId id) {
		super(id);
	}

	@Override
	void add(Archive cu) {
		throw new Error();
	}

	@Override
	public ClassLoader getClassLoader() {
		return new DefaultArchiveClassLoader(this);
	}

}
