package com.github.da;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.github.da.Ext.CC;
import com.github.naf.Application;
import com.github.naf.ApplicationBuilder;

public class DeploymentAnalyserMain {

	public static void main(String... args) throws IOException {
		AnalysisConfiguration ac = new AnalysisConfiguration();
		ac.what = args;
		doit(ac);
	}

	public static AnalysisResult doit(AnalysisConfiguration config) throws IOException {
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

			List<Analysis> jarContent = new LinkedList<>();
			List<Analysis> post = new LinkedList<>();
			List<Analysis> root = new LinkedList<>();
			List<Analysis> classAna = new LinkedList<>();

			Resolver r = a.get(Resolver.class);

			for (Analysis ana : config.analyses) {
				Class<? extends Analyser> clazz = r.resolveClass(ana.beanReference);

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
			for (Analysis ana : jarContent) {
				JarContentProcessor x = (JarContentProcessor) r.resolve(ana.beanReference);
				proc.invokers.add((proc1, path, is) -> x.run(ana, proc1, path, is));
			}
			proc.classAnalyes = new LinkedList<>();
			for (Analysis ana : classAna) {
				ClassAnalysis x = (ClassAnalysis) r.resolve(ana.beanReference);
				proc.classAnalyes.add((v) -> x.run(ana, v));
			}

			for (Analysis ana : root) {
				RootAnalysis aa = (RootAnalysis) r.resolve(ana.beanReference);
				((RootAnalysis) aa).run(ana, proc);
			}

			for (Analysis ana : post) {
				cc.bind(ana.getClass(), ana);

				PostAnalyser aa = (PostAnalyser) r.resolve(ana.beanReference);
				aa.run();

				cc.unbind(ana.getClass());
			}

			// cc.unbind(A3Config.class);
			cc.deactivate();

			return ar;
		}
	}
}
