package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

public class RootApplication {

	@Inject
	@Any
	Instance<FileProcessor> processors;

	@Inject
	@Any
	Instance<ClassAnazlyer> cp;

	@Inject
	AnalysisResult ar;

	public void run(String[] args) throws IOException {
		// TODO Auto-generated method stub

		for (String arg : args) {
			Path p = Paths.get(arg);
			if (!Files.isReadable(p))
				throw new Error();

			Optional<FileProcessor> processor = StreamSupport.stream(processors.spliterator(), false)
					.filter(c -> c.canProcess(p)).reduce((a, b) -> {
						throw new RuntimeException(a + " " + b);
					});

			if (processor.isPresent()) {
				if (Files.isRegularFile(p))
					try (InputStream is = Files.newInputStream(p)) {
						processor.get().process(is);
					}
				else if (Files.isDirectory(p)) {
					processor.get().process(p);
				} else
					throw new Error();
			}
		}

		ClassHierarchy ch = new ClassHierarchy();

		for (ClassModel e : ClassProcessor.classes.values()) {
			for (ClassAnazlyer cp1 : cp)
				cp1.phase2(e, ch);
		}

		ar.put(ClassHierarchy.class, ch);
	}

}
