package com.github.da;

import java.io.IOException;

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

			AnalysisConfiguration configuration = a.get(AnalysisConfiguration.class);

			// for (Class<? extends PostAnalysis> post : config.postAnalyses) {
			// PostAnalysis pa = a.get(post);
			// pa.run(r);
			// }

			CC cc = a.get(CC.class);

			AnalysisResult ar = new AnalysisResult("foo");
			cc.activate(ar);

			a.get(RootApplication.class).run(configuration.what);

			Resolver r = a.get(Resolver.class);
			for (Analysis ana : config.analyses) {
				cc.bind(ana.getClass(), ana);
				r.resolve(ana.beanReference).run();
				cc.unbind(ana.getClass());
			}

			// cc.unbind(A3Config.class);
			cc.deactivate();

			return ar;
		}
	}
}
