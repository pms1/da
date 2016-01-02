package da2;

import java.nio.file.Paths;

import org.junit.Test;

import com.github.da.t.AnalyserConfiguration;
import com.github.da.t.AnalysisConfiguration;
import com.github.da.t.JpaAnalysis;
import com.github.da.t.JpaAnalysis2;
import com.github.da.t.RootAnalysis1Config;
import com.github.da.t.TMain;

public class TMainTest {

	@Test
	public void empty() {
		AnalysisConfiguration config = new AnalysisConfiguration();

		TMain.run(config);
	}

	@Test
	public void t1() {
		AnalysisConfiguration config = new AnalysisConfiguration();

		config = config.with(AnalyserConfiguration.of(JpaAnalysis.class));

		TMain.run(config);

		config = config.with(AnalyserConfiguration.of(JpaAnalysis2.class));
		TMain.run(config);
		config = config.with(AnalyserConfiguration.of(JpaAnalysis.class));
		config = config.with(AnalyserConfiguration.of(JpaAnalysis2.class));
		TMain.run(config);
	}

	@Test
	public void t2() {
		AnalysisConfiguration config = new AnalysisConfiguration();

		config = config.with(new RootAnalysis1Config(Paths.get("foo")));

		TMain.run(config);
	}
}
