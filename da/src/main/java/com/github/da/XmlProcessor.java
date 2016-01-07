package com.github.da;

import org.w3c.dom.Document;

@Analysis
public interface XmlProcessor {
	void run(Archive archive, ResourceId id, Document document);
}
