package com.github.da.t;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.inject.Provider;

@Analysis
public interface ResourceProcessor {

	void run(Path file, Provider<InputStream> pp) throws IOException;

}
