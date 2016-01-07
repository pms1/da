package com.github.da;

import java.nio.file.Path;
import java.util.function.Predicate;

import org.w3c.dom.Document;

@Analysis
public interface XmlProcessor {
	Predicate<Path> filter();

	void run(Archive archive, ResourceId id, Document document);
}
