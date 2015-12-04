package com.github.da;

import org.objectweb.asm.Type;

public class TypeUtil {
	public static Type toArray(Type t) {
		return Type.getType("[" + t.getDescriptor());
	}

	public static boolean isArray(Type t) {
		return t.getDescriptor().startsWith("[");
	}

	public static Type contentType(Type t) {
		String d = t.getDescriptor();
		if (!d.startsWith("["))
			throw new IllegalArgumentException();
		return Type.getType(d.substring(1));
	}

	public static boolean isArrayOf(Type type, Type contentType) {
		if (!isArray(type))
			return false;
		return contentType(type).equals(contentType);
	}
}
