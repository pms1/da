package com.github.da;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Dependent
public class Resolver {
	private @Inject Instance<Object> i;

	public <T> T resolve(BeanReference<T> t) {
		return resolve(t.clazz);
	}

	public <T> T resolve(Class<T> clazz) {
		return (T) i.select(clazz).get();
	}

}
