package com.github.da.t;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.github.da.AnalysisConfiguration;
import com.github.da.Collectors2;
import com.github.da.ConfigurationExtension;
import com.github.da.Ext;
import com.github.da.Ext.AnalyserConfigurationBean;
import com.github.da.Ext.CC1;
import com.github.da.Ext.ConfigurationBean;
import com.github.da.Ext.ConfigurationConfigurationBean;
import com.github.da.Ext.AnalyserListConfigurationBean;
import com.github.da.Ext.ConfigurationBeanVisitor;
import com.github.naf.Application;
import com.github.naf.ApplicationBuilder;
import com.google.common.base.Stopwatch;

public class TMain {
	public static void main(String[] args) {
		AnalysisConfiguration config = new AnalysisConfiguration();
		Stopwatch sw = Stopwatch.createStarted();
		try (Application a = new ApplicationBuilder() //
				.with(new ConfigurationExtension(config))//
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

								Object instantiate = instantiate(bean.getTargetClass(), dconfig2);

								System.err.println("NEXT CONFIG " + instantiate);
								value.add(instantiate);
							}
							value = Collections.unmodifiableList(value);
						} else
							value = null;

						cc1.bind(bean, value);
					}

				});
			}

			return i.select(clazz).get();
		} finally {
			cc1.deactivate();
		}
	}

	private void doit2(AnalysisConfiguration config) {
		// cc1.activate();
		// cc1.bind(C1.class, new C1());
		// cc1.bind(C2.class, new C2());
		// A a = i.select(A1.class).get();

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

		System.err.println("\ndone\n");
	}
}
