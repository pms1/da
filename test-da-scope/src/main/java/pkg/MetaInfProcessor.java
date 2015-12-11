package pkg;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.inject.Provider;

public class MetaInfProcessor implements JarContentProcessor<Void> {

	@Override
	public void accept(AnalysisVisitor av) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(Void config, Processors proc, Path p, Provider<InputStream> is) throws IOException {
	}
}
