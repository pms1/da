package com.github.pms1.c4.classes.annotations;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

import com.github.pms1.asm.annotation.AnnotationData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;

public class AnnotationModel implements Iterable<Map.Entry<Type, AnnotationData>> {

	private Multimap<Type, AnnotationData> data = HashMultimap.create();

	public void add(Type t, AnnotationData fin) {
		data.put(t, fin);
	}

	@Override
	public Iterator<Entry<Type, AnnotationData>> iterator() {
		return Iterators.unmodifiableIterator(data.entries().iterator());
	}

}
