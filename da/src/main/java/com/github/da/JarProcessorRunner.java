package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.inject.Provider;

public interface JarProcessorRunner {
	void run(Processors proc, Path p, Provider<InputStream> is) throws IOException;
}
