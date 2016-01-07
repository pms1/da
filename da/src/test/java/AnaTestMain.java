import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.da.AnalysisConfiguration;
import com.github.da.AnalysisResult;
import com.github.da.ClasspathElementScannerConfig;
import com.github.da.DataModelCreatorConfig;
import com.github.da.ZipScannerConfig;
import com.github.da.TMain;
import com.github.da.jpa.HibernateDB2TypeMapper;
import com.github.da.jpa.HibernateTypeMapper2;
import com.github.da.jpa.JpaModelCreatorConfig;
import com.google.common.base.Stopwatch;

import da.Bottom1;
import da.CompareH2;
import sql.DatabaseModel;

public class AnaTestMain {
	public static void main(String[] args) throws IOException, URISyntaxException {

		// dc.p = Paths.get(
		// "C:/Users/Mirko/.m2/repository/org/wildfly/swarm/examples/wildfly-swarm-example-jaxrs-cdi/1.0.0.Alpha02-SNAPSHOT/wildfly-swarm-example-jaxrs-cdi-1.0.0.Alpha02-SNAPSHOT-swarm.jar");

		Path zip = Paths.get(System.getProperty("user.home")).resolve("ears.zip");

		Stopwatch n = Stopwatch.createUnstarted();
		Stopwatch o = Stopwatch.createUnstarted();

		DataModelCreatorConfig dbmodelGen = DataModelCreatorConfig.newBuilder()
				.withTypeMapper(HibernateDB2TypeMapper.class)//
				.withTypeMapper(HibernateTypeMapper2.class)//
				.build();

		AnalysisConfiguration ac;
		if (false) {
			ac = new AnalysisConfiguration() //
					.with(ClasspathElementScannerConfig.newBuilder().withPath(CompareH2.findDir(Bottom1.class)).build()) //
					.with(dbmodelGen);

			TMain.run(ac);
		}

		ac = new AnalysisConfiguration() //
				.with(ZipScannerConfig.newBuilder().withPath(zip).build()) //
				.with(dbmodelGen);

		TMain.run(ac);

		for (int i = 0; i != 10; ++i) {

			if (false) {
				n.start();

				ZipScannerConfig root = ZipScannerConfig.newBuilder() //
						.withPath(zip) //
						.build();

				AnalysisConfiguration config = new AnalysisConfiguration(); //
				config = config.with(root);

				JpaModelCreatorConfig jpa = JpaModelCreatorConfig.newBuilder().build();
				config = config.with(jpa);

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

				AnalysisResult result = TMain.run(config);
				n.stop();

				o.start();
				AnalysisConfiguration config1 = new AnalysisConfiguration();
				// FIXME config.what = new String[] { zip.toString() };
				dbmodelGen = DataModelCreatorConfig.newBuilder().withTypeMapper(HibernateDB2TypeMapper.class)//
						.withTypeMapper(HibernateTypeMapper2.class)//
						.build();
				config.with(dbmodelGen);
				AnalysisResult ar = TMain.run(config);
				DatabaseModel databaseModel = ar.get(DatabaseModel.class);
				System.err.println("databaseModel=" + databaseModel);
				o.stop();

				System.err.println("I " + (i + 1));
				System.err.println("N " + n);
				System.err.println("O " + o);
			}
		}
	}
}
