package com.github.da;

public class Analysis {
	final BeanReference<? extends Analyser> beanReference;

	public Analysis(Class<? extends Analyser> clazz) {
		this.beanReference = BeanReference.forClass(clazz);
	}
}
