package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Supplier;

import javax.inject.Provider;

@Analysis
public interface ResourceProcessor {

	void run(Supplier<ResourceId> id, Archive parent, Path file, Provider<InputStream> data) throws IOException;

}
