package com.github.da;

class AnalyserMetadata<A, C extends AnalyserConfiguration<A>> {
	final Class<A> analyserClass;
	final Class<C> configClass;
	final Configurator<A, C> configurator;

	AnalyserMetadata(Class<A> analyserClass, Class<C> configClass, Configurator<A, C> configurator) {
		this.analyserClass = analyserClass;
		this.configClass = configClass;
		this.configurator = configurator;
	}

	@Override
	public String toString() {
		return super.toString() + "(analyserClass=" + analyserClass + ", configClass=" + configClass
				+ ", configurator=" + configurator;
	}
}