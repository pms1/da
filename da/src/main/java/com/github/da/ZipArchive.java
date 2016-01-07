package com.github.da;

import java.util.LinkedList;
import java.util.List;

public class ZipArchive extends Archive {

	List<Archive> children = new LinkedList<>();

	@Override
	void add(Archive cu) {
		children.add(cu);
	}

	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

}
