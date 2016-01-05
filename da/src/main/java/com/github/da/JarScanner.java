package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.da.t.All;
import com.google.common.collect.Iterables;

import utils.text.Describable;
import utils.text.Description;

@Include(JarJarProcessor.class)
public class JarScanner implements com.github.da.t.RootAnalysis, Describable {

	@Inject
	@All
	List<JarJarProcessor> jpps;

	@Inject
	JarScannerConfig config;

	@Override
	public void run() throws IOException {

		JarJarProcessor jpp = Iterables.getOnlyElement(jpps);

		jpp.run(config.getPath(), new Provider<InputStream>() {

			@Override
			public InputStream get() {
				try {
					return Files.newInputStream(config.getPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

		});
	}

	@Override
	public void describe(Description d) {
		d.withValue("config.path", config.getPath()) //
				.withList("jpps", jpps);
	}

}
