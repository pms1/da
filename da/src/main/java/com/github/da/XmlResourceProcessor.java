package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.da.t.All;

import utils.text.Describable;
import utils.text.Description;

public class XmlResourceProcessor implements ResourceProcessor, Describable {

	@Inject
	@All
	List<XmlProcessor> xmlProcessors;

	private final static DocumentBuilderFactory factory;

	static {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
	}

	@Override
	public void run(Archive parent, ResourceId id, Provider<InputStream> is) throws IOException {
		if (!id.getPath().getFileName().toString().endsWith(".xml"))
			return;

		Document document;
		try {
			document = factory.newDocumentBuilder().parse(is.get());
		} catch (SAXException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		for (XmlProcessor xmlProcessor : xmlProcessors)
			xmlProcessor.run(parent, id, document);
	}

	@Override
	public void describe(Description d) {
		d.withList("xmlProcessors", xmlProcessors);
	}
}
