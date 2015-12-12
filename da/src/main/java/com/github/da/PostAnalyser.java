package com.github.da;

public interface PostAnalyser<T extends Analysis> extends Analyser<T> {
	void run();
}
