package test.da2;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import com.github.da.AnalyserConfiguration;
import com.github.da.Analysis;
import com.github.da.AnalysisConfiguration;
import com.github.da.ClassResourceProcessor;
import com.github.da.Configurator;
import com.github.da.Include;
import com.github.da.JarResourceProcessor;
import com.github.da.TMain;
import com.github.da.t.All;
import com.github.da.t.AnnotationScannerConfig;
import com.github.da.t.JpaAnalysis;
import com.github.da.t.JpaAnalysis2;
import com.github.da.t.JpaAnalysis3;
import com.github.da.t.RootAnalysis;
import com.github.da.t.RootAnalysis1Config;
import com.github.da.t.RootAnalysis2Config;
import com.github.da.t.RootAnalysis3;

public class TMainTest {

	@Test
	public void empty() throws IOException {
		AnalysisConfiguration config = new AnalysisConfiguration();

		TMain.run(config);
	}

	@Test
	public void t1() throws IOException {
		AnalysisConfiguration config = new AnalysisConfiguration();

		config = config.withAnalysis(AnalyserConfiguration.of(JpaAnalysis.class));

		TMain.run(config);

		config = config.withAnalysis(AnalyserConfiguration.of(JpaAnalysis2.class));
		TMain.run(config);
		config = config.withAnalysis(AnalyserConfiguration.of(JpaAnalysis.class));
		config = config.withAnalysis(AnalyserConfiguration.of(JpaAnalysis2.class));
		TMain.run(config);
		config = config.withAnalysis(AnalyserConfiguration.of(JpaAnalysis3.class));
		TMain.run(config);
	}

	@Test
	public void t2() throws IOException {
		AnalysisConfiguration config = new AnalysisConfiguration();

		config = config.withAnalysis((AnalyserConfiguration<?>) new RootAnalysis1Config(Paths.get("foo")));

		TMain.run(config);

		config = config.withAnalysis(AnalyserConfiguration.of(ClassResourceProcessor.class));
		config = config.withAnalysis(AnalyserConfiguration.of(JarResourceProcessor.class));
		config = config.withAnalysis((AnalyserConfiguration<?>) new AnnotationScannerConfig(3, 1));

		TMain.run(config);
	}

	@Test
	public void t3() throws IOException {
		AnalysisConfiguration config = new AnalysisConfiguration();

		config = config.withAnalysis(AnalyserConfiguration.of(RootAnalysis3.class));
		config = config.withAnalysis((AnalyserConfiguration<?>) new RootAnalysis2Config());

		TMain.run(config);
	}

	static class R1 implements RootAnalysis {
		@Inject
		@All
		List<R1a> r1a;

		@Override
		public void run() throws IOException {
			r1a.forEach(R1a::run);
		}
	}

	static int r1aRuns = 0;

	@Analysis
	@Include(R1.class)
	public static class R1a {
		void run() {
			++r1aRuns;
		}
	}

	public static class R1aConfigurator implements Configurator<R1a, AnalyserConfiguration<R1a>> {
		@Override
		public AnalyserConfiguration<R1a> createConfiguration(Object requirement) {
			if (requirement == R1a.class)
				return AnalyserConfiguration.of(R1a.class);
			else
				return null;
		}
	}

	public static class R2 implements RootAnalysis {
		@Override
		public void run() throws IOException {
		}
	}

	public static class R2Configurator implements Configurator<R2, AnalyserConfiguration<R2>> {
		@Override
		public Collection<Object> getRequirements(AnalyserConfiguration<R2> config) {
			return Arrays.asList(R1a.class);
		}
	}

	@Test
	public void t4() throws IOException {
		AnalysisConfiguration config = new AnalysisConfiguration();

		config.withAnalysis(AnalyserConfiguration.of(R2.class));

		int old = r1aRuns;
		TMain.run(config);

		assertThat(r1aRuns, equalTo(old + 1));
	}

	@Test
	public void t4a() throws IOException {
		AnalysisConfiguration config = new AnalysisConfiguration();

		config.withAnalysis(AnalyserConfiguration.of(R1.class));
		config.withAnalysis(AnalyserConfiguration.of(R2.class));

		int old = r1aRuns;
		TMain.run(config);

		assertThat(r1aRuns, equalTo(old + 1));
	}
}
