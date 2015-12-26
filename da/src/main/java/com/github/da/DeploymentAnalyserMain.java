package com.github.da;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import com.github.da.Ext.CC;
import com.github.naf.Application;
import com.github.naf.ApplicationBuilder;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class DeploymentAnalyserMain {

	public static void main(String... args) throws IOException {
		AnalysisConfiguration ac = new AnalysisConfiguration();
		ac.what = args;
		doit(ac);
	}

	public static AnalysisResult doit(AnalysisConfiguration config) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();
		try (Application a = new ApplicationBuilder() //
				.with(new ConfigurationExtension(config))//
				.with(new Ext())//
				.build()) {
			return a.get(DeploymentAnalyserMain.class).doit2(config);
		} finally {
			sw.stop();
			System.err.println("Anlysis done in " + sw);
		}
	}

	static class InternalAnalysis<C, A extends Analyser<C>> {
		private InternalAnalysis(AnalyserMetadata<C, A> metadata, BeanReference<A> beanReference, C config) {
			this.metadata = metadata;
			this.beanReference = beanReference;
			this.config = config;
		}

		final AnalyserMetadata<C,A> metadata;
		final BeanReference<A> beanReference;
		C config;

		void setConfig(Object config) {
			this.config = (C) config;
		}

		@Override
		public String toString() {
			return super.toString() + "(" + beanReference + "," + config + ")";
		}
	}

	private LinkedHashSet<InternalAnalysis<?, ?>> anas2 = new LinkedHashSet<>();

	@Inject
	private Resolver r;

	InternalAnalysis<?, ? extends Analyser<?>> add(InternalAnalysis<?, ? extends Analyser<?>> ana) {
		Queue<InternalAnalysis<?, ? extends Analyser<?>>> todo = new LinkedList<>();
		todo.add(ana);

		InternalAnalysis<?, ? extends Analyser<?>> result = ana;

		while (!todo.isEmpty()) {
			ana = todo.remove();

			Class<? extends Analyser<?>> anaClass = r.resolveClass(ana.beanReference);

			int merged = 0;

			for (InternalAnalysis<?, ?> a : anas2) {
				if (a.beanReference.equals(ana.beanReference)) {
					AnalyserMetadata<?, ? extends Analyser<?>> metadata = analysersMetadata.getAnalyser(anaClass);

					if (metadata.configurator == null) {
						if (ana.config != null)
							throw new Error();
						if (a.config != null)
							throw new Error();
					} else {
						Object merge = ((Configurator) metadata.configurator).merge(a.config, ana.config);

						if (merge != null) {
							a.setConfig(merge);
							++merged;

							if (result == ana)
								result = a;
						}
					}
				}
			}

			switch (merged) {
			case 0:
				break;
			case 1:
				continue;
			default:
				throw new Error();
			}

			if (!anas2.add(ana))
				continue;

			for (Include i : anaClass.getAnnotationsByType(Include.class)) {
				todo.add(createAnalysis(i));
			}

		}

		return result;
	}

	private <C, A extends Analyser<C>> InternalAnalysis<C, A> createAnalysis(Include inc) {
		return create(BeanReference.forClass((Class<A>) inc.value()), null);
	}

	static class Helper {
		@Any
		@Inject
		Instance<Analyser<?>> analysers;
	}

	@Inject
	CC cc;

	Multimap<InternalAnalysis<?, ?>, Object> requirements = HashMultimap.create();
	Multimap<InternalAnalysis<?, ?>, Object> provides2 = HashMultimap.create();

	<C, A extends Analyser<C>> void addRequirements(InternalAnalysis<C, A> ana) {
		AnalyserMetadata<C, A> md = analysersMetadata.getAnalyser(r.resolveClass(ana.beanReference));

		if (md.configurator != null) {

			Collection<Object> reqs = md.configurator.getRequirements(ana.config);

			reqs.forEach((r) -> requirements.put(ana, r));

			for (Object r : reqs) {
				int count = 0;
				for (AnalyserMetadata<?, ?> a : analysersMetadata.all) {
					AnalyserMetadata<Object, Analyser<Object>> a1 = (AnalyserMetadata<Object, Analyser<Object>>) a;

					if (a.configurator == null)
						continue;

					Object config = a.configurator.createConfiguration(r);
					if (config == null)
						continue;

					InternalAnalysis<?, ? extends Analyser<?>> internalAnalysis = create(
							BeanReference.forClass(a1.analyser), config);

					internalAnalysis = add(internalAnalysis);

					provides2.put(internalAnalysis, r);

					++count;
				}

				if (count == 0)
					throw new Error("No provider for requirement: " + r);
				else if (count != 1)
					throw new Error("Multiple providers for requirement: " + r);
			}
		}
	}

	class AnalysisSorter {
		Set<Object> provided = new HashSet<>();

		<T> List<InternalAnalysis<?, ? extends T>> resolve(List<InternalAnalysis<?, ? extends T>> anas) {
			List<InternalAnalysis<?, ? extends T>> result = new LinkedList<>();

			Set<InternalAnalysis<?, ?>> done = new HashSet<>();
			for (;;) {
				boolean any = false;
				for (InternalAnalysis<?, ? extends T> ana : anas) {
					if (done.contains(ana))
						continue;

					boolean all = true;
					for (Object r : requirements.get(ana)) {
						if (!provided.contains(r)) {
							all = false;
							break;
						}
					}

					if (all) {
						result.add(ana);

						provided.addAll(provides2.get(ana));

						any = true;
						done.add(ana);
					}
				}

				if (!any) {
					if (result.size() == anas.size())
						break;
					else {
						Set<Object> unresolved = new HashSet<>(anas);
						unresolved.removeAll(done);
						throw new Error(result.size() + " " + anas.size() + " unresolved: " + unresolved);
					}
				}
			}

			return result;
		}
	}

	AnalysisResult doit2(AnalysisConfiguration config) throws IOException {
		for (Analysis<?, ? extends Analyser<?>> ana : config.analyses)
			add(create((Analysis) ana));

		Set<InternalAnalysis<?, ?>> done = new HashSet<>();
		for (;;) {
			LinkedHashSet<InternalAnalysis<?, ?>> old = new LinkedHashSet<>(anas2);
			for (InternalAnalysis<?, ?> ana : old) {
				if (!done.add(ana))
					continue;

				addRequirements(ana);
			}
			if (old.size() == anas2.size())
				break;
		}

		List<InternalAnalysis<?, ? extends JarContentProcessor<?>>> jarContent = new LinkedList<>();
		List<InternalAnalysis<?, ? extends PostAnalyser<?>>> post = new LinkedList<>();
		List<InternalAnalysis<?, ? extends RootAnalysis<?>>> root = new LinkedList<>();
		List<InternalAnalysis<?, ? extends ClassAnalysis<?>>> classAna = new LinkedList<>();

		for (InternalAnalysis ana : anas2) {
			Class<? extends Analyser<?>> clazz = r.resolveClass(ana.beanReference);

			if (JarContentProcessor.class.isAssignableFrom(clazz)) {
				jarContent.add(ana);
			}
			if (PostAnalyser.class.isAssignableFrom(clazz)) {
				post.add(ana);
			}
			if (RootAnalysis.class.isAssignableFrom(clazz)) {
				root.add(ana);
			}
			if (ClassAnalysis.class.isAssignableFrom(clazz)) {
				classAna.add(ana);
			}
		}

		AnalysisResult ar = new AnalysisResult("foo");
		cc.activate(ar);

		AnalysisSorter r1 = new AnalysisSorter();

		Processors proc = new Processors();
		proc.invokers = new LinkedList<>();
		for (InternalAnalysis<?, ? extends JarContentProcessor<?>> ana : jarContent) {
			proc.invokers.add(instantiate(ana));
			}

		proc.classAnalyes = new LinkedList<>();
		for (InternalAnalysis<?, ? extends ClassAnalysis<?>> ana : r1.resolve(classAna)) {
			proc.classAnalyes.add(instantiate(ana));
		}

		for (InternalAnalysis<?, ? extends RootAnalysis<?>> ana : new AnalysisSorter().resolve(root)) {
			instantiate(ana).run(proc);
		}

		for (InternalAnalysis<?, ?> ana : r1.resolve(post)) {
			if (ana.config != null)
				cc.bind(ana.getClass(), ana.config);

			PostAnalyser aa = (PostAnalyser) r.resolve(ana.beanReference);
			aa.run();

			if (ana.config != null)
				cc.unbind(ana.getClass());
		}

		cc.deactivate();

		return ar;
	}

	private <C, A extends Analyser<C>> A instantiate(InternalAnalysis<C, A> ana) {
		if(ana.metadata.config!=null) 
			cc.bind(ana.metadata.config, ana.config);
		
		try {
		A a = r.resolve(ana.beanReference);
		return a;
		} finally {
			if(ana.metadata.config!=null) 
						cc.unbind(ana.metadata.config);
		}
	}

	@Inject
	BeanManager bm;

	// Multimap<Class<?>, Class<Analyser<?>>> providers;
	//
	// private Multimap<Class<?>, Class<Analyser<?>>> getProviders() {
	// if (providers != null)
	// return providers;
	//
	// Multimap<Class<?>, Class<Analyser<?>>> p = HashMultimap.create();
	//
	// for (Bean<?> b : bm.getBeans(analyserType)) {
	// for (Provide i : b.getBeanClass().getAnnotationsByType(Provide.class)) {
	// p.put(i.value(), (Class<Analyser<?>>) b.getBeanClass());
	// }
	// }
	// return providers = p;
	//
	// }

	static final Type analyserType = new TypeLiteral<Analyser<?>>() {
	}.getType();

	<T, U extends Analyser<T>> Type configuratorType(Class<T> config, Class<U> analyser) {
		return new TypeToken<Configurator<T, U>>() {
		}.where(new TypeParameter<T>() {
		}, config).where(new TypeParameter<U>() {
		}, analyser).getType();
	}

	@Inject
	@Any
	Instance<Configurator<?, ?>> configurators;

	static class AnalyserMetadata<C, A extends Analyser<C>> {
		final Class<A> analyser;
		final Class<C> config;
		final Configurator<C, A> configurator;
		final Collection<Provide> provides;

		AnalyserMetadata(Class<A> analyser, Collection<Provide> provides, Class<C> config,
				Configurator<C, A> configurator) {
			Objects.requireNonNull(analyser);
			this.analyser = analyser;
			Objects.requireNonNull(provides);
			this.provides = provides;
			Objects.requireNonNull(config);
			this.config = config;
			Objects.requireNonNull(configurator);
			this.configurator = configurator;
		}

		AnalyserMetadata(Class<A> analyser, Collection<Provide> provides) {
			Objects.requireNonNull(analyser);
			this.analyser = analyser;
			Objects.requireNonNull(provides);
			this.provides = provides;
			this.config = null;
			this.configurator = null;
		}

		@Override
		public String toString() {
			return "Analyser(" + analyser + "," + config + "," + configurator + ")";
		}
	}

	<C, A extends Analyser<C>, D extends Configurator<C, A>> AnalyserMetadata<C, A> createAnalyserMetadata(
			Class<A> beanClass) {
		TypeToken<A> analyserToken = TypeToken.of(beanClass);

		@SuppressWarnings("unchecked")
		Class<C> configClass = (Class<C>) analyserToken.resolveType(Analyser.class.getTypeParameters()[0]).getType();

		List<Provide> provides = Arrays.asList(beanClass.getAnnotationsByType(Provide.class));

		if (!configClass.equals(Void.class)) {
			Type configuratorType = configuratorType(configClass, beanClass);

			Set<Bean<?>> beans = bm.getBeans(configuratorType);
			switch (beans.size()) {
			case 1:
				@SuppressWarnings("unchecked")
				Class<D> configuratorClass = (Class<D>) Iterables.getOnlyElement(beans).getBeanClass();
				Instance<D> select = configurators.select(configuratorClass);
				if (select.isAmbiguous())
					throw new Error();
				else if (select.isUnsatisfied())
					throw new Error();

				return new AnalyserMetadata<C, A>(beanClass, provides, configClass, select.get());
			case 0:
				throw new Error("No configurators found for " + configuratorType);
			default:
				throw new Error("Multiple configurators found for " + configuratorType + ": " + beans);
			}
		} else {
			return new AnalyserMetadata<C, A>(beanClass, provides);
		}
	}

	static class AnalysersMetadata {
		final Collection<AnalyserMetadata<?, ?>> all;
		final Map<Class<?>, AnalyserMetadata<?, ?>> byAnalyser;
		final Multimap<Class<?>, AnalyserMetadata<?, ?>> byProvideClass;

		AnalysersMetadata(Collection<AnalyserMetadata<?, ?>> metadata) {
			Objects.requireNonNull(metadata);
			all = Collections.unmodifiableCollection(new ArrayList<>(metadata));
			byAnalyser = Collections.unmodifiableMap(
					metadata.stream().collect(Collectors.toMap((m) -> m.analyser, Function.identity())));

			byProvideClass = Multimaps.unmodifiableMultimap(metadata.stream().collect(Collectors2
					.toMultimapFlatened((m) -> m.provides.stream().map((p) -> p.value()), Function.identity())));
		}

		public <C, A extends Analyser<C>> AnalyserMetadata<C, A> getAnalyser(Class<A> analyserClass) {
			@SuppressWarnings("unchecked")
			AnalyserMetadata<C, A> result = (AnalyserMetadata<C, A>) byAnalyser.get(analyserClass);
			return result;
		}

		public <C, A extends Analyser<C>> AnalyserMetadata<C, A> getProvider(Class<?> providerClass) {
			@SuppressWarnings("unchecked")
			AnalyserMetadata<C, A> result = (AnalyserMetadata<C, A>) Iterables
					.getOnlyElement(byProvideClass.get(providerClass), null);
			return result;
		}
	}

	AnalysersMetadata analysersMetadata;

	@PostConstruct
	void initAnalysers() {
		List<AnalyserMetadata<?, ?>> metadata = new LinkedList<>();
		for (Bean<?> b : bm.getBeans(analyserType)) {
			metadata.add(createAnalyserMetadata((Class) b.getBeanClass()));
		}
		analysersMetadata = new AnalysersMetadata(metadata);
	}

	<C, A extends Analyser<C>> InternalAnalysis<C, A> create(BeanReference<A> beanReference, C config) {
		Class<A> resolveClass = r.resolveClass(beanReference);
		return new InternalAnalysis<C, A>(analysersMetadata.getAnalyser(resolveClass), beanReference, config);
	}

	<C extends Analysis<C, A>, A extends Analyser<C>> InternalAnalysis<C, A> create2(C analysis) {
		Class<A> resolveClass = r.resolveClass(analysis.beanReference);
		return new InternalAnalysis<C, A>(analysersMetadata.getAnalyser(resolveClass), analysis.beanReference,
				analysis);
	}

	<C extends Analysis<C, A>, A extends Analyser<C>> InternalAnalysis<C, A> create(C analysis) {
		Class<A> resolveClass = r.resolveClass(analysis.beanReference);
		return new InternalAnalysis<C, A>(analysersMetadata.getAnalyser(resolveClass), analysis.beanReference,
				analysis);

	}

	// private Class<Analysis<? extends Analyser<?>>> getProvider(Class<?>
	// value) {
	// Multimap<Class<?>, Class<Analyser<?>>> p = getProviders();
	// Collection<Class<Analyser<?>>> collection = p.get(value);
	// switch (collection.size()) {
	// case 0:
	// return null;
	// case 1:
	// return Iterables.getOnlyElement(collection);
	// default:
	// throw new Error("C=" + collection);
	// }
	// }
}
