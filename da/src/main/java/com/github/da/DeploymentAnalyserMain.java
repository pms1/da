package com.github.da;

import java.io.IOException;

import com.github.naf.Application;
import com.github.naf.ApplicationBuilder;

public class DeploymentAnalyserMain {

	public static void main(String... args) throws IOException {
		doit(args);
	}

	public static ClassHierarchy doit(String... args) throws IOException {
		try (Application a = new ApplicationBuilder().build()) {
			return a.get(RootApplication.class).run(args);
		}
	}

}
