package com.github.da;

import org.objectweb.asm.Type;

import com.google.common.base.Preconditions;

public final class AsmIds {
	private AsmIds() {

	}

	public static ClassId forClass(String name) {
		Type type = Type.getObjectType(name);
		Preconditions.checkArgument(type.getSort() == Type.OBJECT, "Not an object type: {0}", name);

		return new ClassId(name);
	}
}
