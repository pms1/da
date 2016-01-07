package com.github.da;

public class WebArchive extends Archive {

	public WebArchive(ResourceId id) {
		super(id);
	}

	@Override
	void add(Archive cu) {
		throw new Error();
	}

	@Override
	public ClassLoader getClassLoader() {

		throw new Error();
	}

}
