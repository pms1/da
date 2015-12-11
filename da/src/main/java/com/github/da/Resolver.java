package com.github.da;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.google.common.collect.Iterables;

@Dependent
public class Resolver {
	private @Inject Instance<Object> i;

	public <T> T resolve(BeanReference<T> t) {
		return resolve(t.clazz);
	}

	public <T> T resolve(Class<T> clazz) {
		return (T) i.select(clazz).get();
	}

	@Inject
	BeanManager bm;

	public <T> Class<T> resolveClass(BeanReference<T> beanReference) {
		return (Class<T>) Iterables.getOnlyElement(bm.getBeans(beanReference.clazz)).getBeanClass();
	}

}
