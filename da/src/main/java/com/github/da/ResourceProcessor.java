package com.github.da;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Provider;

@Analysis
public interface ResourceProcessor {

	void run(ResourceId id, Archive parent, Provider<InputStream> data) throws IOException;

}
