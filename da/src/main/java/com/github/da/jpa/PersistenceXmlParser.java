package com.github.da.jpa;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.github.da.Archive;
import com.github.da.ResourceId;
import com.github.da.ResourceProcessor;

public class PersistenceXmlParser implements ResourceProcessor {

	final static private Path persistenceXml = Paths.get("META-INF/persistence.xml");

	private final static JAXBContext jaxbContext_2_0;
	private final static JAXBContext jaxbContext_2_1;
	private final static DocumentBuilderFactory factory;

	static {
		try {
			jaxbContext_2_0 = JAXBContext
					.newInstance(com.github.da.jpa.model.persistence_2_0.Persistence.class.getPackage().getName());
			jaxbContext_2_1 = JAXBContext
					.newInstance(com.github.da.jpa.model.persistence_2_1.Persistence.class.getPackage().getName());
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run(ResourceId id, Archive parent, Provider<InputStream> data) throws IOException {
		if (!id.getPath().equals(persistenceXml))
			return;

		System.err.println("FILE " + id.getPath());

		try {
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document parse = documentBuilder.parse(data.get());
			String version = parse.getDocumentElement().getAttribute("version");

			PersistenceXmlUnits pu;
			switch (version) {
			case "2.0":
				pu = parse_2_0(parent, parse);
				break;
			case "2.1":
				pu = parse_2_1(parent, parse);
				break;
			default:
				throw new Error("unhandled jpa version: " + version);
			}

			parent.put(PersistenceXmlUnits.class, pu);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private PersistenceXmlUnits parse_2_0(Archive cu, Document parse) throws JAXBException {
		Unmarshaller u = jaxbContext_2_0.createUnmarshaller();
		com.github.da.jpa.model.persistence_2_0.Persistence c = u
				.unmarshal(parse, com.github.da.jpa.model.persistence_2_0.Persistence.class).getValue();

		List<PersistenceXmlUnit> result = new LinkedList<>();
		for (com.github.da.jpa.model.persistence_2_0.Persistence.PersistenceUnit pu : c.getPersistenceUnit()) {
			if (!pu.getMappingFile().isEmpty())
				throw new UnsupportedOperationException();
			if (!pu.getJarFile().isEmpty())
				throw new UnsupportedOperationException();

			// default is to scan; the default=true in the XSD is misleading.
			// See
			// https://hibernate.atlassian.net/browse/HHH-8364 for a detailed
			// discussion
			boolean isExcludeUnlistedClasses;
			if (pu.isExcludeUnlistedClasses() != null)
				isExcludeUnlistedClasses = pu.isExcludeUnlistedClasses();
			else
				isExcludeUnlistedClasses = false;

			result.add(new PersistenceXmlUnit(new ArrayList<>(pu.getClazz()), isExcludeUnlistedClasses));
		}
		return new PersistenceXmlUnits(result);
	}

	private PersistenceXmlUnits parse_2_1(Archive cu, Document parse) throws JAXBException {
		Unmarshaller u = jaxbContext_2_1.createUnmarshaller();
		com.github.da.jpa.model.persistence_2_1.Persistence c = u
				.unmarshal(parse, com.github.da.jpa.model.persistence_2_1.Persistence.class).getValue();

		List<PersistenceXmlUnit> result = new LinkedList<>();
		for (com.github.da.jpa.model.persistence_2_1.Persistence.PersistenceUnit pu : c.getPersistenceUnit()) {

			if (!pu.getMappingFile().isEmpty())
				throw new UnsupportedOperationException();
			if (!pu.getJarFile().isEmpty())
				throw new UnsupportedOperationException();

			// default is to scan; the default=true in the XSD is misleading.
			// See
			// https://hibernate.atlassian.net/browse/HHH-8364 for a detailed
			// discussion
			boolean isExcludeUnlistedClasses;
			if (pu.isExcludeUnlistedClasses() != null)
				isExcludeUnlistedClasses = pu.isExcludeUnlistedClasses();
			else
				isExcludeUnlistedClasses = false;

			result.add(new PersistenceXmlUnit(new ArrayList<>(pu.getClazz()), isExcludeUnlistedClasses));
		}
		return new PersistenceXmlUnits(result);
	}

}
