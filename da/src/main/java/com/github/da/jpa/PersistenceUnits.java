package com.github.da.jpa;

import java.util.Collection;
import java.util.Collections;

public class PersistenceUnits {
	private final Collection<PersistenceUnit> units;

	/* package */public PersistenceUnits(Collection<PersistenceUnit> units) {
		this.units = Collections.unmodifiableCollection(units);
	}

	public Collection<PersistenceUnit> getUnits() {
		return units;
	}
}
