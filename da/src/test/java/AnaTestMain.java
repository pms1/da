import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.da.AnalysisConfiguration;
import com.github.da.AnalysisResult;
import com.github.da.DataModelCreatorConfig;
import com.github.da.DeploymentAnalyserMain;
import com.github.da.HibernateDB2TypeMapper;
import com.github.da.HibernateTypeMapper;
import com.github.da.JarScannerConfig;
import com.github.da.JpaModelCreatorConfig;
import com.google.common.base.Stopwatch;

public class AnaTestMain {
	public static void main(String[] args) throws IOException {

		// dc.p = Paths.get(
		// "C:/Users/Mirko/.m2/repository/org/wildfly/swarm/examples/wildfly-swarm-example-jaxrs-cdi/1.0.0.Alpha02-SNAPSHOT/wildfly-swarm-example-jaxrs-cdi-1.0.0.Alpha02-SNAPSHOT-swarm.jar");
		// dc.p = Paths.get("c:/temp/ears.zip");

		Path zip = Paths.get(System.getProperty("user.home")).resolve("ears.zip");

		Stopwatch n = Stopwatch.createUnstarted();
		Stopwatch o = Stopwatch.createUnstarted();

		for (int i = 0; i != 10; ++i) {
			n.start();

			JarScannerConfig root = JarScannerConfig.newBuilder() //
					.withPath(zip) //
					.build();

			AnalysisConfiguration config = new AnalysisConfiguration(); //
			config = config.withAnalysis(root);

			JpaModelCreatorConfig jpa = JpaModelCreatorConfig.newBuilder().build();
			config = config.withAnalysis(jpa);

			// .withAnalysis(JarAnalysis.class, dc) //
			// .withAnalysis(ClassProcessor.class) //
			// .withAnalysis(MetaInfProcessor.class) //
			// .withAnalysis(JarJarProcessor.class) //
			// .withAnalysis(AnnotationScanner.class, new
			// AnnotationScannerConfig())
			// //
			// .withAnalysis(AnnotationScanner.class, new
			// AnnotationScannerConfig())
			// //
			// .withAnalysis(A1.class) //
			// .withAnalysis(A2.class) //
			// .withAnalysis(A3.class, new A3Config(TM1.class)) //
			// .withAnalysis(A3.class, new A3Config(TM2.class));

			AnalysisResult result = DeploymentAnalyserMain.doit(config);
			n.stop();

			o.start();
			AnalysisConfiguration config1 = new AnalysisConfiguration();
			config.what = new String[] { zip.toString() };
			DataModelCreatorConfig dbmodelGen = DataModelCreatorConfig.newBuilder()
					.withTypeMapper(HibernateDB2TypeMapper.class)//
					.withTypeMapper(HibernateTypeMapper.class)//
					.build();
			config.withAnalysis(dbmodelGen);
			AnalysisResult ar = DeploymentAnalyserMain.doit(config);
			o.stop();

			System.err.println("I " + (i + 1));
			System.err.println("N " + n);
			System.err.println("O " + o);
		}
	}
}