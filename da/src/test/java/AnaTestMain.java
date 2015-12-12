import java.io.IOException;
import java.nio.file.Paths;

import com.github.da.AnalysisConfiguration;
import com.github.da.AnalysisResult;
import com.github.da.DeploymentAnalyserMain;
import com.github.da.JarScannerConfig;

public class AnaTestMain {
	public static void main(String[] args) throws IOException {

		// dc.p = Paths.get(
		// "C:/Users/Mirko/.m2/repository/org/wildfly/swarm/examples/wildfly-swarm-example-jaxrs-cdi/1.0.0.Alpha02-SNAPSHOT/wildfly-swarm-example-jaxrs-cdi-1.0.0.Alpha02-SNAPSHOT-swarm.jar");
		// dc.p = Paths.get("c:/temp/ears.zip");

		JarScannerConfig root = JarScannerConfig.newBuilder().withPath(Paths.get("c:/temp/ears.zip")).build();

		AnalysisConfiguration config = new AnalysisConfiguration(); //

		config = config.withAnalysis(root);

		// .withAnalysis(JarAnalysis.class, dc) //
		// .withAnalysis(ClassProcessor.class) //
		// .withAnalysis(MetaInfProcessor.class) //
		// .withAnalysis(JarJarProcessor.class) //
		// .withAnalysis(AnnotationScanner.class, new AnnotationScannerConfig())
		// //
		// .withAnalysis(AnnotationScanner.class, new AnnotationScannerConfig())
		// //
		// .withAnalysis(A1.class) //
		// .withAnalysis(A2.class) //
		// .withAnalysis(A3.class, new A3Config(TM1.class)) //
		// .withAnalysis(A3.class, new A3Config(TM2.class));

		AnalysisResult result = DeploymentAnalyserMain.doit(config);

	}
}
