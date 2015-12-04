package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface FileProcessor {
	boolean canProcess(Path p);

	default void process(InputStream is) throws IOException {
		throw new UnsupportedOperationException();
	}

	default void process(Path p) throws IOException {
		throw new UnsupportedOperationException();
	}

}
