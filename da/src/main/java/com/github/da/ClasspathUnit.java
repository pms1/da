package com.github.da;

import java.util.Arrays;
import java.util.Collection;

public class ClasspathUnit extends GenericData {

	public <T> Collection<T> getAll(Class<T> class1) {
		return Arrays.asList(get(class1));
	}

}
