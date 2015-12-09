package da;

import java.io.IOException;

import com.github.da.AnalysisConfiguration;
import com.github.da.DeploymentAnalyserMain;

public class TM2 {
	public static void main(String[] args) throws IOException {
		AnalysisConfiguration config = new AnalysisConfiguration();
		config.what = new String[] { "c:/temp/ears.zip" };
		DeploymentAnalyserMain.doit(config);
	}
}
