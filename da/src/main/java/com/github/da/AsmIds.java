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

	public static MethodId forMethod(String name, String desc) {
		Type type = Type.getMethodType(desc);
		Preconditions.checkArgument(type.getSort() == Type.METHOD, "Not a method type: {0}", desc);

		if (name.contains("\t") || desc.contains("\t"))
			throw new UnsupportedOperationException();

		return new MethodId(name + "\t" + desc);
	}

	public static FieldId forField(String name) {
		return new FieldId(name);
	}
}
