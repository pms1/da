package com.github.da;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

import com.github.da.AV1.Ann;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;

public class AnnotationModel implements Iterable<Map.Entry<Type, Map<String, Ann>>> {

	private Multimap<Type, Map<String, Ann>> data = HashMultimap.create();

	public void add(Type t, Map<String, Ann> fin) {
		data.put(t, fin);
	}

	@Override
	public Iterator<Entry<Type, Map<String, Ann>>> iterator() {
		return Iterators.unmodifiableIterator(data.entries().iterator());
	}

}
