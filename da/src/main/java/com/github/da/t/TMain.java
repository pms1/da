package com.github.da.t;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.weld.injection.FieldInjectionPoint;

import com.github.da.AnalysisResult;
import com.github.da.Collectors2;
import com.github.da.Configuration;
import com.github.da.ConfigurationExtension;
import com.github.da.Ext;
import com.github.da.Ext.AnalyserConfigurationBean;
import com.github.da.Ext.AnalyserListBean;
import com.github.da.Ext.AnalyserListConfigurationBean;
import com.github.da.Ext.CC;
import com.github.da.Ext.CC1;
import com.github.da.Ext.ConfigurationBean;
import com.github.da.Ext.ConfigurationBeanVisitor;
import com.github.da.Ext.ConfigurationConfigurationBean;
import com.github.da.Include;
import com.github.naf.Application;
import com.github.naf.ApplicationBuilder;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import utils.text.Description;

public class TMain {
	public static void main(String[] args) throws IOException {
		AnalysisConfiguration config = new AnalysisConfiguration();
		Stopwatch sw = Stopwatch.createStarted();
		try (Application a = new ApplicationBuilder() //
				.with(new ConfigurationExtension(new com.github.da.AnalysisConfiguration()))//
				.with(new Ext())//
				.build()) {
			a.get(TMain.class).doit2(config);
		} finally {
			sw.stop();
			System.err.println("Anlysis done in " + sw);
		}
	}

	private @Inject Instance<Object> i;
	private @Inject CC1 cc1;

	private @Inject Ext ext;
	private @Inject BeanManager bm;

	private <T> void traverse(AnalyserConfiguration<?> a, Consumer<AnalyserConfiguration<?>> consumer) {
		traverse(a, consumer, new HashSet<>());
	}

	private <T> void traverse(AnalyserConfiguration<?> a, Consumer<AnalyserConfiguration<?>> consumer,
			Set<Bean<?>> visited) {
		Bean<T> bean = (Bean<T>) bm.resolve(bm.getBeans(a.getAnalyser()));

		if (!visited.add(bean))
			return;

		consumer.accept(a);

		for (ConfigurationBean<?> dep : ext.getDependencies(bean)) {
			dep.accept(new ConfigurationBeanVisitor() {

				@Override
				public <T1> void visit(ConfigurationConfigurationBean<T1> bean) {
				}

				@Override
				public <T1> void visit(AnalyserConfigurationBean<T1> bean) {
				}

				@Override
				public <T1> void visit(AnalyserListConfigurationBean<T1> bean) {
				}

				@Override
				public <T1> void visit(AnalyserListBean<T1> bean) {
					anas.stream().filter((a) -> bean.getTargetClass().isAssignableFrom(a.getAnalyser()))
							.forEach((a) -> traverse(a, consumer, visited));
				}
			});
		}
	}

	<T> T instantiate(AnalyserConfiguration<T> config) {
		return instantiate(config, new HashMap<>());
	}

	<T> T instantiate(AnalyserConfiguration<T> config,
			Map<AnalyserConfiguration<?>, Set<Consumer<Object>>> inCreation) {

		Object[] configs = new Object[] { config };

		Bean<T> bean = (Bean<T>) bm.resolve(bm.getBeans(config.getAnalyser()));

		Collection<ConfigurationBean<?>> deps = ext.getDependencies(bean);

		inCreation.put(config, new HashSet<>());

		try {
			cc1.activate();

			for (ConfigurationBean<?> dep : deps) {
				dep.accept(new ConfigurationBeanVisitor() {

					@Override
					public <T1> void visit(ConfigurationConfigurationBean<T1> bean) {

						Object config = Arrays.stream(configs).filter((c) -> c.getClass().equals(bean.getBeanClass()))
								.collect(Collectors2.findOnly());

						cc1.bind(dep, config);
					}

					@Override
					public <T1> void visit(AnalyserConfigurationBean<T1> bean) {
						Object config;
						try {
							config = Arrays.stream(configs)
									.filter((c) -> c.getClass().equals(bean.getConfigurationClass()))
									.collect(Collectors2.findOnly());
						} catch (NoSuchElementException e) {
							throw new Error("Missing configuration of type " + bean.getConfigurationClass());
						}

						Object dconfig = bean.extract(config);

						Object value;

						if (dconfig != null) {
							// FIXME
							value = instantiate((AnalyserConfiguration) dconfig);
							if (!bean.getTargetClass().equals(value.getClass()))
								throw new Error();
						} else {
							value = null;
						}

						cc1.bind(bean, value);
					}

					@Override
					public <T1> void visit(AnalyserListConfigurationBean<T1> bean) {
						Object config = Arrays.stream(configs)
								.filter((c) -> c.getClass().equals(bean.getConfigurationClass()))
								.collect(Collectors2.findOnly());

						List<?> dconfig = bean.extract(config);

						List<Object> value;

						if (dconfig != null) {
							value = new LinkedList<>();
							for (Object dconfig2 : dconfig) {
								AnalyserConfiguration c;
								if (dconfig2 instanceof AnalyserConfiguration) {
									c = (AnalyserConfiguration) dconfig2;
									if (!bean.getTargetClass().isAssignableFrom(c.getAnalyser()))
										throw new Error();
								} else {
									c = AnalyserConfiguration.of(bean.getTargetClass());
								}

								value.add(instantiate(c));
							}
							value = Collections.unmodifiableList(value);
						} else
							value = null;

						cc1.bind(bean, value);
					}

					@Override
					public <T1> void visit(AnalyserListBean<T1> bean) {

						List<AnalyserConfiguration<?>> create2 = (List) create2(anas, bean.getTargetClass());

						List result = new LinkedList<>();
						for (AnalyserConfiguration<?> c : create2) {
							Set<Consumer<Object>> set = inCreation.get(c);
							if (set != null) {
								int i = result.size();
								set.add((a) -> result.set(i, a));
								result.add(null);
							} else
								result.add(instantiate(c));
						}
						cc1.bind(bean, result);
					}

				});
			}

			T result = i.select(config.getAnalyser()).get();

			inCreation.remove(config).forEach((c) -> c.accept(result));

			return result;
		} finally {
			cc1.deactivate();
		}

	}

	@Inject
	AnalysersMetadata analysersMetadata;
	private List<AnalyserConfiguration<?>> anas;

	class Resolver {
		private final List<AnalyserConfiguration<?>> anas = new LinkedList<>();

		private LinkedHashSet<Object> unresolvedRequirements = new LinkedHashSet<>();
		private LinkedHashSet<Include> unresolvedInclude = new LinkedHashSet<>();

		private final Multimap<AnalyserConfiguration<?>, Object> requirements = HashMultimap.create();
		private final Map<Object, AnalyserConfiguration<?>> requirementResolution = new HashMap<>();

		<A, C extends AnalyserConfiguration<A>> C addAnalyserConfiguration(C c) {
			AnalyserMetadata<A, C> metadata = analysersMetadata.get(c);

			System.err.println(c + " -> " + metadata);
			if (metadata != null && metadata.configurator != null) {

				for (Iterator<AnalyserConfiguration<?>> ia = anas.iterator(); ia.hasNext();) {
					AnalyserConfiguration<?> a = ia.next();
					if (analysersMetadata.get(a) != metadata)
						continue;

					C merged = metadata.configurator.merge(metadata.configClass.cast(a), c);
					if (merged == null)
						continue;

					if (merged == a)
						return merged;

					// replace configuration to add by merged configuration
					c = merged;
					ia.remove();
					requirements.removeAll(a);
					C c1 = c;
					requirementResolution.replaceAll((k, v) -> v == a ? c1 : v);
					break;
				}

				for (Object requirement : metadata.configurator.getRequirements(c)) {
					unresolvedRequirements.add(requirement);
					requirements.put(c, requirement);
				}
			}

			Collections.addAll(unresolvedInclude, c.getAnalyser().getAnnotationsByType(Include.class));

			anas.add(c);

			return c;
		}

		void finish() {
			while (!unresolvedRequirements.isEmpty() || !unresolvedInclude.isEmpty()) {
				while (!unresolvedRequirements.isEmpty()) {
					Set<Object> todo = unresolvedRequirements;
					unresolvedRequirements = new LinkedHashSet<>();

					for (Object req : todo) {
						if (requirementResolution.containsKey(req))
							continue;

						Set<Configurator<?, ?>> configurators = new HashSet<>();

						for (AnalyserMetadata<?, ?> c : analysersMetadata) {
							if (c.configurator == null)
								continue;

							AnalyserConfiguration<?> config = c.configurator.createConfiguration(req);
							if (config == null)
								continue;

							System.err.println("Solving requirement " + req + " with " + config);
							config = addAnalyserConfiguration(config);

							Object old = requirementResolution.putIfAbsent(req, config);
							if (old != null)
								throw new Error(
										"Duplicate resolution of requirement '" + req + "': " + config + " " + old);

							configurators.add(c.configurator);
						}

						switch (configurators.size()) {
						case 0:
							throw new Error("Unresolved requirement: " + req);
						case 1:
							break;
						default:
							throw new Error("Ambigous requirement: " + req + ", " + configurators);
						}
					}
				}

				if (!anas.containsAll(requirements.keySet()))
					throw new Error();
				if (!anas.containsAll(requirementResolution.values())) {
					anas.stream().forEach(x -> System.out.println("ANA " + x));
					requirementResolution.values().stream().forEach(x -> System.out.println("RES " + x));
					throw new Error();
				}

				if (!unresolvedInclude.isEmpty()) {
					Set<Include> todo = unresolvedInclude;
					unresolvedInclude = new LinkedHashSet<>();

					for (Include inc : todo) {

						if (anas.stream().anyMatch((a) -> a.getAnalyser().equals(inc.value())))
							continue;

						AnalyserMetadata<?, ?> metadata = analysersMetadata.get(inc.value());
						if (metadata != null && !(metadata.configClass == null
								|| AnalyserConfiguration.class.equals(metadata.configClass)))
							throw new Error("" + inc.value());

						addAnalyserConfiguration(AnalyserConfiguration.of(inc.value()));
					}
				}
			}
		}

		List<AnalyserConfiguration<?>> sort(List<AnalyserConfiguration<?>> todo) {
			todo = new LinkedList<>(todo);

			Set<Object> resolved = new HashSet<>();

			LinkedList<AnalyserConfiguration<?>> result = new LinkedList<>();

			while (!todo.isEmpty()) {
				int oldSize = todo.size();

				for (Iterator<AnalyserConfiguration<?>> ia = todo.iterator(); ia.hasNext();) {
					AnalyserConfiguration<?> a = ia.next();

					if (requirements.get(a).stream().allMatch(req -> resolved.contains(req))) {
						// System.err.println("RESOLVED " + a);
						ia.remove();
						result.add(a);

						traverse(a, (c) -> {
							if (!anas.contains(c))
								throw new Error();

							// System.err.println(" INCLUDED " + c);

							requirementResolution.entrySet().forEach((e) -> {
								if (e.getValue() == c)
									resolved.add(e.getKey());
							});

						});
					}
				}

				if (oldSize == todo.size()) {
					for (AnalyserConfiguration<?> a : todo) {
						requirements.get(a).stream().filter(req -> !resolved.contains(req))
								.forEach((r) -> System.err.println("UNRESOLVED " + a + " - " + r));
					}
					throw new Error("Unresolved dependencies");
				}
			}

			return result;
		}

	}

	Resolver r;

	@Inject
	CC cc;

	public void doit2(AnalysisConfiguration config) throws IOException {

		System.err.println(analysersMetadata.toString());

		r = new Resolver();
		for (AnalyserConfiguration<?> c : config.configs)
			r.addAnalyserConfiguration(c);

		r.finish();

		this.anas = r.anas;

		System.err.println("FINAL");
		r.anas.stream().forEach((x) -> System.err.println("  " + x));
		System.err.println("CONFIG -> REQ");
		r.requirements.entries().stream().forEach((x) -> System.err.println("  " + x));
		System.err.println("REQ -> CONFIG");
		r.requirementResolution.entrySet().stream().forEach((x) -> System.err.println("  " + x));

		System.err.println("START");
		AnalysisResult ar = new AnalysisResult("foo");
		cc.activate(ar);
		try {
			for (AnalyserConfiguration<RootAnalysis> a : create2(anas, RootAnalysis.class)) {
				RootAnalysis rootAnaylsis = instantiate(a);
				System.err.print("running " + new Description().describe(rootAnaylsis));
				rootAnaylsis.run();
			}
		} finally {
			cc.deactivate();
		}
		System.err.println("END");
		// cc1.activate();
		// cc1.bind(C1.class, new C1());
		// cc1.bind(C2.class, new C2());
		// A a = i.select(A1.class).get();

		if (false) {
			{
				A2 a2 = instantiate(new A2Config());
				System.err.println("A2=" + a2);
			}

			// {
			// C2 c2 = new C2();
			// c2.a2 = Arrays.asList(new A2Config());
			// A1 a1 = instantiate(new C1(), c2);
			// System.err.println("A1=" + a1);
			// }
			//
			// {
			// A1 a1 = instantiate(A1.class, null, new C1(), new C2());
			// System.err.println("A1=" + a1);
			// }
			//
			// {
			// A3 a3 = instantiate(AnayserConfiguratiinA3.class, null);
			// System.err.println("A3=" + a3);
			// }
			//
			// {
			// A31Config a31c = new A31Config();
			// a31c.a41c = new A41Config();
			// A31 a31 = instantiate(a31c);
			// System.err.println("A3=" + a31);
			// }
			//
			// {
			// TypeMappersConfig c = new TypeMappersConfig();
			// c = c.withTypeMapper(AnalyserConfiguration.of(TM1.class));
			// c = c.withTypeMapper(new TM2Config());
			// TMUserConfig tmUserConfig = new TMUserConfig();
			// tmUserConfig.tmConfig = c;
			// TMUser user = instantiate(TMUser.class, null, tmUserConfig);
			// System.err.println("U " + asString(user));
			// }
		}

		System.err.println("\ndone\n");
	}

	private <T> List<T> create(List<AnalyserConfiguration<?>> anas, Class<T> class1) {
		return create2(anas, class1).stream().map(a -> instantiate(a)).collect(Collectors.toList());
	}

	private <T> List<AnalyserConfiguration<T>> create2(List<AnalyserConfiguration<?>> anas, Class<T> class1) {
		List<AnalyserConfiguration<?>> filtered = anas.stream().filter((a) -> class1.isAssignableFrom(a.getAnalyser()))
				.collect(Collectors.toList());

		return (List) r.sort(filtered);
	}

	static class TMUser {
		@Inject
		@Configured
		TypeMappers typeMappers;
	}

	@Configuration
	static class TMUserConfig extends AnalyserConfiguration<TMUser> {

		TMUserConfig() {
			super(TMUser.class);
		}

		TypeMappersConfig tmConfig;

	}

	private String asString(Object o) {
		if (o == null)
			return String.valueOf((Object) null);

		if (o instanceof List)
			return o.toString();

		String s = o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));

		Bean<? extends Object> bean = bm.resolve(bm.getBeans(o.getClass()));
		if (bean == null)
			return s;

		boolean first = true;
		for (InjectionPoint ip : bean.getInjectionPoints()) {
			if (ip instanceof FieldInjectionPoint) {
				FieldInjectionPoint fip = (FieldInjectionPoint) ip;
				AnnotatedField af = fip.getAnnotated();
				Field f = af.getJavaMember();
				if (first) {
					first = false;
					s += "(";
				}

				try {
					f.setAccessible(true);
					s += f.getName() + "=" + asString(f.get(o));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				throw new Error();
			}
		}

		if (!first)
			s += ")";
		return s;

	}

	public static void run(com.github.da.t.AnalysisConfiguration config) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();
		try (Application a = new ApplicationBuilder() //
				.with(new ConfigurationExtension(new com.github.da.AnalysisConfiguration()))//
				.with(new Ext())//
				.build()) {
			a.get(TMain.class).doit2(config);
		} finally {
			sw.stop();
			System.err.println("Anlysis done in " + sw);
		}

	}
}
