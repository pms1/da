package com.github.da;

public class Analysis<C, A extends Analyser<C>> {
	final BeanReference<A> beanReference;

	public Analysis(Class<A> clazz) {
		this.beanReference = BeanReference.forClass(clazz);
	}
}
