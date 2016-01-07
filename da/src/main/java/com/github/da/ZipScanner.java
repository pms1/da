package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.da.t.All;
import com.google.common.collect.Iterables;

import utils.text.Describable;
import utils.text.Description;

@Include(JarResourceProcessor.class)
public class ZipScanner implements com.github.da.t.RootAnalysis, Describable {

	@Inject
	@All
	List<JarResourceProcessor> jpps;

	@Inject
	ZipScannerConfig config;

	@Inject
	AnalysisResult ar;

	@Override
	public void run() throws IOException {

		JarResourceProcessor jpp = Iterables.getOnlyElement(jpps);

		List<Archive> archives = new LinkedList<>();

		Archive cu = new Archive(ResourceId.create(config.getPath())) {

			@Override
			public <T> void put(Class<T> class1, T data1) {
				throw new Error();
			}

			@Override
			protected <T> void put(Class<T> class1, Supplier<T> t) {
				throw new Error();
			}

			@Override
			public void add(Archive cu) {
				archives.add(cu);
			}

			@Override
			public ClassLoader getClassLoader() {
				throw new Error();
			}
		};

		jpp.run(ResourceId.create(config.getPath()), cu, new Provider<InputStream>() {

			@Override
			public InputStream get() {
				try {
					return Files.newInputStream(config.getPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

		});

		ZipArchive zip = (ZipArchive) Iterables.getOnlyElement(archives);

		ar.put(DeploymentArtifacts.class, new DeploymentArtifacts(zip.children));

	}

	@Override
	public void describe(Description d) {
		d.withValue("config.path", config.getPath()) //
				.withList("jpps", jpps);
	}

}
