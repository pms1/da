package com.github.da;

public class Analysis<T extends Analyser> {
	final BeanReference<T> beanReference;

	public Analysis(Class<T> clazz) {
		this.beanReference = BeanReference.forClass(clazz);
	}
}
