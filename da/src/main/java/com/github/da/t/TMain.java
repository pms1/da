package com.github.da.t;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.weld.injection.FieldInjectionPoint;

import com.github.da.Collectors2;
import com.github.da.Configuration;
import com.github.da.ConfigurationExtension;
import com.github.da.Ext;
import com.github.da.Ext.AnalyserConfigurationBean;
import com.github.da.Ext.AnalyserListBean;
import com.github.da.Ext.AnalyserListConfigurationBean;
import com.github.da.Ext.CC1;
import com.github.da.Ext.ConfigurationBean;
import com.github.da.Ext.ConfigurationBeanVisitor;
import com.github.da.Ext.ConfigurationConfigurationBean;
import com.github.naf.Application;
import com.github.naf.ApplicationBuilder;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import utils.text.Description;

public class TMain {
	public static void main(String[] args) {
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

	<T> T instantiate(Class<T> clazz, Object... configs) {

		Bean<T> bean = (Bean<T>) bm.resolve(bm.getBeans(clazz));

		System.err.println("BEAN " + bean);

		Collection<ConfigurationBean<?>> deps = ext.getDependencies(bean);

		try {
			cc1.activate();

			for (ConfigurationBean<?> dep : deps) {
				System.err.println("DEP " + dep);

				dep.accept(new ConfigurationBeanVisitor() {

					@Override
					public <T1> void visit(ConfigurationConfigurationBean<T1> bean) {

						Object config = Arrays.stream(configs).filter((c) -> c.getClass().equals(bean.getBeanClass()))
								.collect(Collectors2.findOnly());

						cc1.bind(dep, config);
					}

					@Override
					public <T1> void visit(AnalyserConfigurationBean<T1> bean) {
						System.err.println("CONFIG CLASS " + bean.getConfigurationClass());

						Object config = Arrays.stream(configs)
								.filter((c) -> c.getClass().equals(bean.getConfigurationClass()))
								.collect(Collectors2.findOnly());

						System.err.println("CONFIG " + config);

						Object dconfig = bean.extract(config);

						Object value;

						if (dconfig != null) {
							value = instantiate(bean.getTargetClass(), dconfig);
						} else {
							value = null;
						}

						cc1.bind(bean, value);
					}

					@Override
					public <T1> void visit(AnalyserListConfigurationBean<T1> bean) {
						System.err.println("CONFIG CLASS " + bean.getConfigurationClass());

						Object config = Arrays.stream(configs)
								.filter((c) -> c.getClass().equals(bean.getConfigurationClass()))
								.collect(Collectors2.findOnly());

						System.err.println("CONFIG " + config);

						List<?> dconfig = bean.extract(config);

						List<Object> value;

						if (dconfig != null) {
							value = new LinkedList<>();
							for (Object dconfig2 : dconfig) {
								System.err.println("NEXT CONFIG " + dconfig2);

								Class<?> c;
								if (dconfig2 instanceof AnalyserConfiguration) {
									c = ((AnalyserConfiguration<?>) dconfig2).getAnalyser();
									if (!bean.getTargetClass().isAssignableFrom(c))
										throw new Error();
								} else {
									c = bean.getTargetClass();
								}

								System.err.println("NEXT CONFIG " + c + " " + dconfig2);

								Object instantiate = instantiate(c, dconfig2);

								System.err.println("NEXT CONFIG " + instantiate);
								value.add(instantiate);
							}
							value = Collections.unmodifiableList(value);
						} else
							value = null;

						cc1.bind(bean, value);
					}

					@Override
					public <T1> void visit(AnalyserListBean<T1> bean) {
						cc1.bind(bean, create(anas, bean.getTargetClass()));
					}

				});
			}

			return i.select(clazz).get();
		} finally {
			cc1.deactivate();
		}
	}

	@Inject
	AnalysersMetadata analysersMetadata;
	private List<AnalyserConfiguration<?>> anas;

	class Resolver {
		private final List<AnalyserConfiguration<?>> anas = new LinkedList<>();

		private Set<Object> openRequirements = new HashSet<>();
		private final Set<Object> resolvedRequirements = new HashSet<>();

		private final Multimap<AnalyserConfiguration<?>, Object> config2req = HashMultimap.create();
		private final Map<Object, AnalyserConfiguration<?>> resolved = new HashMap<>();

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
					config2req.removeAll(a);
					C c1 = c;
					resolved.replaceAll((k, v) -> v == a ? c1 : v);
					break;
				}

				for (Object requirement : metadata.configurator.getRequirements(c)) {
					openRequirements.add(requirement);
					config2req.put(c, requirement);
				}
			}

			anas.add(c);

			return c;
		}

		void finish() {
			while (!openRequirements.isEmpty()) {
				Set<Object> todo = openRequirements;
				openRequirements = new HashSet<>();

				for (Object req : todo) {
					if (!resolvedRequirements.add(req))
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

						Object old = resolved.putIfAbsent(req, config);
						if (old != null)
							throw new Error("req=" + req + " " + config + " " + old);

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

			if (!anas.containsAll(config2req.keySet()))
				throw new Error();
			if (!anas.containsAll(resolved.values())) {
				anas.stream().forEach(x -> System.out.println("ANA " + x));
				resolved.values().stream().forEach(x -> System.out.println("RES " + x));
				throw new Error();
			}
		}

	}

	public void doit2(AnalysisConfiguration config) {

		System.err.println(analysersMetadata.toString());

		Resolver r = new Resolver();
		for (AnalyserConfiguration<?> c : config.configs)
			r.addAnalyserConfiguration(c);

		r.finish();

		for (Object o : r.anas) {
			System.err.println("FINAL " + o);
		}

		this.anas = r.anas;

		System.err.println("CONFIG -> REQ");
		r.config2req.entries().stream().forEach(System.err::println);
		System.err.println("REQ -> CONFIG");
		r.resolved.entrySet().stream().forEach(System.err::println);

		System.err.println("START");
		for (RootAnalysis rootAnaylsis : create(anas, RootAnalysis.class)) {
			System.err.println(new Description().describe(rootAnaylsis));
			rootAnaylsis.run();
		}
		System.err.println("END");
		// cc1.activate();
		// cc1.bind(C1.class, new C1());
		// cc1.bind(C2.class, new C2());
		// A a = i.select(A1.class).get();

		if (false) {
			{
				A2 a2 = instantiate(A2.class, new A2Config());
				System.err.println("A2=" + a2);
			}

			{
				C2 c2 = new C2();
				c2.a2 = Arrays.asList(new A2Config(), new A2Config());
				A1 a1 = instantiate(A1.class, new C1(), c2);
				System.err.println("A1=" + a1);
			}

			{
				A1 a1 = instantiate(A1.class, new C1(), new C2());
				System.err.println("A1=" + a1);
			}

			{
				A3 a3 = instantiate(A3.class);
				System.err.println("A3=" + a3);
			}

			{
				A31Config a31c = new A31Config();
				a31c.a41c = new A41Config();
				A31 a31 = instantiate(A31.class, a31c);
				System.err.println("A3=" + a31);
			}

			{
				TypeMappersConfig c = new TypeMappersConfig();
				c = c.withTypeMapper(AnalyserConfiguration.of(TM1.class));
				c = c.withTypeMapper(new TM2Config());
				TMUserConfig tmUserConfig = new TMUserConfig();
				tmUserConfig.tmConfig = c;
				TMUser user = instantiate(TMUser.class, tmUserConfig);
				System.err.println("U " + asString(user));
			}
		}

		System.err.println("\ndone\n");
	}

	private <T> List<T> create(List<AnalyserConfiguration<?>> anas, Class<T> class1) {
		List<T> instances = new LinkedList<>();

		for (AnalyserConfiguration<?> a : anas) {
			System.err.println("C " + class1 + " " + a.getAnalyser());
			if (class1.isAssignableFrom(a.getAnalyser())) {
				instances.add(instantiate(a.getAnalyser().asSubclass(class1), a));
			}
		}

		return instances;
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

	public static void run(com.github.da.t.AnalysisConfiguration config) {
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
