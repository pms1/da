package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.inject.Inject;
import javax.inject.Provider;

public class ClasspathElementScanner implements RootAnalysis<ClasspathElementScannerConfig> {

	@Inject
	ClasspathElementScannerConfig config;
	
	@Override
	public void run(Processors proc) throws IOException {

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
				for (JarContentProcessor<?> i : proc.invokers)
					i.run(proc, file, pp);

				// TODO Auto-generated method stub
				return super.visitFile(file, attrs);
			}
		});
	}

}
