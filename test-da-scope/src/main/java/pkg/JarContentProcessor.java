package pkg;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.inject.Provider;

public interface JarContentProcessor<T> extends Analysis {
	default void accept(AnalysisVisitor av) {
		av.visit(this);
	}

	void run(T config, Processors proc, Path p, Provider<InputStream> is) throws IOException;
}
