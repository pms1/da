package com.github.da.jpa;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class PersistenceUnits implements Iterable<PersistenceUnit> {
	private final Collection<PersistenceUnit> units;

	/* package */public PersistenceUnits(Collection<PersistenceUnit> units) {
		this.units = Collections.unmodifiableCollection(units);
	}

	public Collection<PersistenceUnit> getUnits() {
		return units;
	}

	@Override
	public Iterator<PersistenceUnit> iterator() {
		return units.iterator();
	}
}
