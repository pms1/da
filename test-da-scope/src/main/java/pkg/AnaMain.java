package pkg;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import com.github.naf.Application;
import com.github.naf.ApplicationBuilder;

import pkg.AnaConfig.Ana;
import pkg.Ext.CC;

public class AnaMain {
	public static void main(String[] args) throws IOException {
		for (int i = 0; i != 50; ++i) {
			long start = System.currentTimeMillis();
			Path p = Paths.get("w:/priv/workspaces/sitescan/workspace/da.git/da/target/test-classes/da/TM1.class");

			DirAnalysisConfig dc = new DirAnalysisConfig();
			dc.p = Paths.get("w:/priv/workspaces/sitescan/workspace/da.git/da/target/test-classes/");

			AnaConfig ac = new AnaConfig() //
					.withAnalysis(DirAnalysis.class, dc) //
					.withAnalysis(ClassProcessor.class) //
					.withAnalysis(MetaInfProcessor.class) //
					.withAnalysis(AnnotationScanner.class, new AnnotationScannerConfig()) //
					.withAnalysis(A1.class) //
					.withAnalysis(A2.class) //
					.withAnalysis(A3.class, new A3Config(TM1.class)) //
					.withAnalysis(A3.class, new A3Config(TM2.class));

			AnaResult ar = new AnaMain().run(ac);

			dc = new DirAnalysisConfig();
			dc.p = Paths.get(
					"C:/Users/Mirko/.m2/repository/org/wildfly/swarm/examples/wildfly-swarm-example-jaxrs-cdi/1.0.0.Alpha02-SNAPSHOT/wildfly-swarm-example-jaxrs-cdi-1.0.0.Alpha02-SNAPSHOT-swarm.jar");
			dc.p = Paths.get("c:/temp/ears.zip");

			ac = new AnaConfig() //
					.withAnalysis(JarAnalysis.class, dc) //
					.withAnalysis(ClassProcessor.class) //
					.withAnalysis(MetaInfProcessor.class) //
					.withAnalysis(JarJarProcessor.class) //
					.withAnalysis(AnnotationScanner.class, new AnnotationScannerConfig()) //
					.withAnalysis(AnnotationScanner.class, new AnnotationScannerConfig()) //
					.withAnalysis(A1.class) //
					.withAnalysis(A2.class) //
					.withAnalysis(A3.class, new A3Config(TM1.class)) //
					.withAnalysis(A3.class, new A3Config(TM2.class));

			ar = new AnaMain().run(ac);

			long end = System.currentTimeMillis();
			System.err.println("DUR " + (end - start));
		}
	}

	AnaResult run(AnaConfig ac) throws IOException {
		try (Application a = new ApplicationBuilder().with(new Ext()).build()) {
			CC cc = a.get(CC.class);

			List<Ana> jarContent = new LinkedList<>();
			List<Ana> post = new LinkedList<>();
			List<Ana> root = new LinkedList<>();
			List<Ana> classAna = new LinkedList<>();

			for (Ana ana : ac.analyses) {
				if (JarContentProcessor.class.isAssignableFrom(ana.clazz)) {
					jarContent.add(ana);
				}
				if (PostAnalysis.class.isAssignableFrom(ana.clazz)) {
					post.add(ana);
				}
				if (RootAnalysis.class.isAssignableFrom(ana.clazz)) {
					root.add(ana);
				}
				if (ClassAnalysis.class.isAssignableFrom(ana.clazz)) {
					classAna.add(ana);
				}
			}

			AnaResult ar = new AnaResult("foo");
			cc.activate(ar);

			Processors proc = new Processors();
			proc.invokers = new LinkedList<>();
			for (Ana ana : jarContent) {
				JarContentProcessor x = (JarContentProcessor) a.get(ana.clazz);
				proc.invokers.add((proc1, path, is) -> x.run(ana.config, proc1, path, is));
			}
			proc.classAnalyes = new LinkedList<>();
			for (Ana ana : classAna) {
				ClassAnalysis x = (ClassAnalysis) a.get(ana.clazz);
				proc.classAnalyes.add((v) -> x.run(ana.config, v));
			}

			for (Ana ana : root) {
				Analysis aa = a.get(ana.clazz);
				((RootAnalysis) aa).run(ana.config, proc);
			}

			for (Ana ana : post) {
				if (ana.config != null)
					cc.bind(ana.config.getClass(), ana.config);
				Analysis aa = a.get(ana.clazz);
				((PostAnalysis) aa).run();
				if (ana.config != null)
					cc.unbind(ana.config.getClass());
			}

			// cc.unbind(A3Config.class);
			cc.deactivate();

			return ar;
		}

	}
}
