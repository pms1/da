package com.github.da;

import java.io.IOException;

public interface RootAnalysis<T> extends Analyser<T> {

	void run(T t, Processors p) throws IOException;

}
