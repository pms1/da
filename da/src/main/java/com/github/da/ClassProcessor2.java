package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.inject.Provider;

import org.objectweb.asm.ClassReader;

public class ClassProcessor2 implements JarContentProcessor<Void> {

	@Override
	public void run(Processors proc, Path p, Provider<InputStream> is) throws IOException {
		if (!p.getFileName().toString().endsWith(".class"))
			return;

		ClassReader reader = new ClassReader(is.get());

		for (ClassAnalysis<?> p1 : proc.classAnalyes)
			p1.run(reader);
	}

}
