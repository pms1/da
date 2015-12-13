package com.github.da;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.da.Ext.CC;
import com.github.naf.Application;
import com.github.naf.ApplicationBuilder;
import com.google.common.base.Stopwatch;

public class DeploymentAnalyserMain {

	public static void main(String... args) throws IOException {
		AnalysisConfiguration ac = new AnalysisConfiguration();
		ac.what = args;
		doit(ac);
	}

	public static AnalysisResult doit(AnalysisConfiguration config) throws IOException {
		return new DeploymentAnalyserMain().doit2(config);
	}
	
	AnalysisResult doit2(AnalysisConfiguration config) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();
		try (Application a = new ApplicationBuilder() //
				.with(new ConfigurationExtension(config))//
				.with(new Ext())//
				.build()) {

			// AnalysisConfiguration configuration =
			// a.get(AnalysisConfiguration.class);
			//
			// // for (Class<? extends PostAnalysis> post : config.postAnalyses)
			// {
			// // PostAnalysis pa = a.get(post);
			// // pa.run(r);
			// // }
			//
			// CC cc = a.get(CC.class);
			//
			// AnalysisResult ar = new AnalysisResult("foo");
			// cc.activate(ar);
			//
			// a.get(RootApplication.class).run(configuration.what);
			//
			// Resolver r = a.get(Resolver.class);
			// for (Analysis ana : config.analyses) {
			// cc.bind(ana.getClass(), ana);
			// r.resolve(ana.beanReference).run();
			// cc.unbind(ana.getClass());
			// }
			//
			// // cc.unbind(A3Config.class);
			// cc.deactivate();

			Resolver r = a.get(Resolver.class);

			Set<Class<? extends Analyser<?>>> included = new HashSet<>();
			LinkedList<Class<?>> todo = new LinkedList<>();

			for (Analysis<?> ana : config.analyses)
				for (Include i : r.resolveClass(ana.beanReference).getAnnotationsByType(Include.class))
					Collections.addAll(todo, i.value());

			while (!todo.isEmpty()) {
				Class<?> clazz = todo.removeFirst();
				if (!included.add((Class) clazz.asSubclass(Analyser.class)))
					continue;

				for (Include i : clazz.getAnnotationsByType(Include.class))
					Collections.addAll(todo, i.value());
			}

			System.err.println("INCLUDE " + included);

			class InternalAnalysis {
				InternalAnalysis(BeanReference<? extends Analyser<?>> beanReference, Object config) {
					this.beanReference = beanReference;
					this.config = config;
				}

				final BeanReference<? extends Analyser<?>> beanReference;
				final Object config;
			}
			List<InternalAnalysis> anas = new LinkedList<>();
			for (Analysis<?> ana : config.analyses)
				anas.add(new InternalAnalysis(ana.beanReference, ana));

			for (Class<? extends Analyser<?>> inc : included)
				anas.add(new InternalAnalysis(BeanReference.forClass(inc), null));

			List<InternalAnalysis> jarContent = new LinkedList<>();
			List<InternalAnalysis> post = new LinkedList<>();
			List<InternalAnalysis> root = new LinkedList<>();
			List<InternalAnalysis> classAna = new LinkedList<>();

			for (InternalAnalysis ana : anas) {
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

			CC cc = a.get(CC.class);

			AnalysisResult ar = new AnalysisResult("foo");
			cc.activate(ar);

			Processors proc = new Processors();
			proc.invokers = new LinkedList<>();
			for (InternalAnalysis ana : jarContent) {
				JarContentProcessor x = (JarContentProcessor) r.resolve(ana.beanReference);
				proc.invokers.add((proc1, path, is) -> x.run(ana.config, proc1, path, is));
			}
			proc.classAnalyes = new LinkedList<>();
			for (InternalAnalysis ana : classAna) {
				ClassAnalysis x = (ClassAnalysis) r.resolve(ana.beanReference);
				proc.classAnalyes.add((v) -> x.run(ana.config, v));
			}

			for (InternalAnalysis ana : root) {
				RootAnalysis aa = (RootAnalysis) r.resolve(ana.beanReference);
				((RootAnalysis) aa).run(ana.config, proc);
			}

			for (InternalAnalysis ana : post) {
				if (ana.config != null)
					cc.bind(ana.getClass(), ana.config);

				PostAnalyser aa = (PostAnalyser) r.resolve(ana.beanReference);
				aa.run();

				if (ana.config != null)
					cc.unbind(ana.getClass());
			}

			// cc.unbind(A3Config.class);
			cc.deactivate();

			return ar;
		} finally {
			sw.stop();
			System.err.println("Anlysis done in " + sw);
		}
	}
}
