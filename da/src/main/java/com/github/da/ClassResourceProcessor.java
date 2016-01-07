package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.objectweb.asm.ClassReader;

import com.github.da.t.All;

import utils.text.Describable;
import utils.text.Description;

public class ClassResourceProcessor implements ResourceProcessor, Describable {

	@Inject
	@All
	List<ClassProcessor> classProcessors;

	@Override
	public void run(Archive parent, ResourceId id, Provider<InputStream> is) throws IOException {
		if (!id.getPath().getFileName().toString().endsWith(".class"))
			return;

		ClassReader reader = new ClassReader(is.get());

		for (ClassProcessor classProcessor : classProcessors)
			classProcessor.run(parent, id, reader);
	}

	@Override
	public void describe(Description d) {
		d.withList("classProcessors", classProcessors);
	}
}
