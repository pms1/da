package com.github.da.t;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.common.collect.Iterators;

import utils.TypeUtils;

@ApplicationScoped
class AnalysersMetadata implements Iterable<AnalyserMetadata<?, ?>> {
	private List<AnalyserMetadata<?, ?>> all;

	static <A, C extends AnalyserConfiguration<A>> AnalyserMetadata<A, C> createMetadata(
			Configurator<A, C> configurator) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Class<A> analysisClass = (Class) TypeUtils.resolve(configurator.getClass(),
				Configurator.class.getTypeParameters()[0]);
		@SuppressWarnings({ "unchecked" })
		Class<C> configClass = (Class<C>) TypeUtils
				.getRawType(TypeUtils.resolve(configurator.getClass(), Configurator.class.getTypeParameters()[1]));

		return new AnalyserMetadata<A, C>(analysisClass, configClass, configurator);
	}

	@Inject
	void setConfigurators(@Any Instance<Configurator<?, ?>> configurators) {
		all = new LinkedList<>();
		for (Configurator<?, ?> c : configurators) {

			AnalyserMetadata<?, ?> md = createMetadata(c);
			System.err.println("MD " + md);
			all.add(md);
		}
	}

	public <A, C extends AnalyserConfiguration<A>> AnalyserMetadata<A, C> get(AnalyserConfiguration<A> c) {
		// FIXME: use streams
		AnalyserMetadata<A, C> result = null;
		for (AnalyserMetadata<?, ?> a : all) {
			if (a.analyserClass.equals(c.getAnalyser()) && a.configClass.equals(c.getClass())) {
				if (result != null)
					throw new Error();
				result = (AnalyserMetadata<A, C>) a;
			}
		}
		return result;
	}

	public <A, C extends AnalyserConfiguration<A>> AnalyserMetadata<A, C> get(Class<A> c) {
		// FIXME: use streams
		AnalyserMetadata<A, C> result = null;
		for (AnalyserMetadata<?, ?> a : all) {
			if (a.analyserClass.equals(c)) {
				if (result != null)
					throw new Error();
				result = (AnalyserMetadata<A, C>) a;
			}
		}
		return result;
	}

	@Override
	public Iterator<AnalyserMetadata<?, ?>> iterator() {
		return Iterators.unmodifiableIterator(all.iterator());
	}
}