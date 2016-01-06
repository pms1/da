package com.github.da.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PersistenceXmlUnits implements Iterable<PersistenceXmlUnit> {
	private List<PersistenceXmlUnit> units;

	public PersistenceXmlUnits(List<PersistenceXmlUnit> units) {
		this.units = Collections.unmodifiableList(new ArrayList<>(units));
	}

	public List<PersistenceXmlUnit> getUnits() {
		return units;
	}

	@Override
	public Iterator<PersistenceXmlUnit> iterator() {
		return units.iterator();
	}
}
