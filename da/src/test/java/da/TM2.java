package da;

import java.io.IOException;
import java.nio.file.Paths;

import com.github.da.AnalysisConfiguration;
import com.github.da.ZipScannerConfig;
import com.github.da.TMain;

public class TM2 {
	public static void main(String[] args) throws IOException {
		AnalysisConfiguration config = new AnalysisConfiguration();
		config.with(ZipScannerConfig.newBuilder()
				.withPath(Paths.get(System.getProperty("user.home")).resolve("ears.zip")).build());

		TMain.run(config);
	}
}
