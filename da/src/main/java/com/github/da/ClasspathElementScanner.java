package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.da.t.All;

public class ClasspathElementScanner implements com.github.da.t.RootAnalysis {

	@Inject
	ClasspathElementScannerConfig config;

	@Inject
	@All
	List<ResourceProcessor> proc;

	@Inject
	AnalysisResult ar;

	@Override
	public void run() throws IOException {
		Archive archive = new DirectoryArchive();

		Files.walkFileTree(config.getPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				Provider<InputStream> pp = new Provider<InputStream>() {

					@Override
					public InputStream get() {
						try {
							return Files.newInputStream(file);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}

				};

				for (ResourceProcessor i : proc)
					i.run(Lazy.of(() -> ResourceId.create(ResourceId.create(config.getPath()), file)), archive,
							config.getPath().relativize(file), pp);

				return super.visitFile(file, attrs);
			}
		});

		ar.put(DeploymentArtifacts.class, new DeploymentArtifacts(Collections.singletonList(archive)));
	}

}
