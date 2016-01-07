package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.github.da.t.All;
import com.google.common.collect.Iterables;

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

	private Predicate<Path> filter;
	private LinkedHashMap<XmlProcessor, Predicate<Path>> filtered;

	@PostConstruct
	void postConstruct() {
		filtered = new LinkedHashMap<>();
		xmlProcessors.stream().forEach((p) -> {
			Predicate<Path> filter = p.filter();
			filtered.put(p, filter);
		});

		Collection<Predicate<Path>> collect = new ArrayList<>(filtered.values());

		if (collect.stream().anyMatch((p) -> p == null))
			filter = (p) -> true;
		else if (collect.size() == 1)
			filter = Iterables.getOnlyElement(collect);
		else
			filter = (p) -> {
				for (Predicate<Path> f : collect)
					if (f.test(p))
						return true;
				return false;
			};

	}

	@Override
	public void run(Archive archive, ResourceId id, Provider<InputStream> is) throws IOException {
		if (!filter.test(id.getPath()))
			return;

		Document document;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {

				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					throw new Error("xml tried to resolved externaly: " + publicId + " " + systemId);
				}

			});
			document = builder.parse(is.get());
		} catch (SAXException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		filtered.entrySet().forEach((e) -> {
			if (e.getValue() == null || e.getValue().test(id.getPath()))
				e.getKey().run(archive, id, document);
		});
	}

	@Override
	public void describe(Description d) {
		d.withList("xmlProcessors", xmlProcessors);
	}
}
