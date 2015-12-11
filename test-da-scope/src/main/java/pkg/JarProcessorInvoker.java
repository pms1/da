package pkg;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.inject.Provider;

public interface JarProcessorInvoker {
	void run(Processors proc, Path p, Provider<InputStream> is) throws IOException;
}
