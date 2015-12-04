package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.inject.Inject;

public class DirProcessor implements FileProcessor {

	@Override
	public boolean canProcess(Path p) {
		return Files.isDirectory(p);
	}

	@Inject
	ClassProcessor cp;

	@Override
	public void process(Path p) throws IOException {
		Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				if (file.toString().endsWith(".class")) {
					try (InputStream is = Files.newInputStream(file)) {
						cp.process(is);
					}
				}

				return super.visitFile(file, attrs);
			}
		});
	}

}
