package com.github.pms1.asm.annotation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.da.TypeUtil;

/*package*/ class InternalAnnotationBuilder extends AnnotationVisitor {

	InternalAnnotationBuilder() {
		super(Opcodes.ASM5);
	}

	private List<Object> anon = new LinkedList<>();
	private Type anonType = null;

	private void anon(Type type, Object value) {
		if (anonType == null)
			anonType = type;
		else if (!anonType.equals(type))
			throw new Error();

		anon.add(value);
	}

	@Override
	public final void visit(String name, Object value) {
		if (value == null)
			throw new Error();

		Type type;
		if (value.getClass().equals(String.class)) {
			type = Type.getType(String.class);
		} else if (value.getClass().equals(Boolean.class)) {
			type = Type.BOOLEAN_TYPE;
		} else if (value.getClass().equals(Integer.class)) {
			type = Type.INT_TYPE;
		} else if (value.getClass().equals(Long.class)) {
			type = Type.LONG_TYPE;
		} else if (value.getClass().equals(Type.class)) {
			type = Type.getType(Class.class);
		} else {
			throw new Error("name=" + name + " value=" + value + " " + value.getClass());
		}

		if (name == null) {
			anon(type, value);
		} else {
			add(name, new AnnotationValue(type, value));
		}
	}

	@Override
	public final void visitEnum(String name, String desc, String value) {
		if (name == null) {
			anon(Type.getType(desc), value);
			return;
		}
		add(name, new AnnotationValue(Type.getType(desc), value));
	}

	@Override
	public final AnnotationVisitor visitAnnotation(String name, String desc) {
		String outerName = name;
		Type t = Type.getType(desc);
		InternalAnnotationBuilder outer = this;

		return new AnnotationBuilder() {
			@Override
			public void visitEnd(AnnotationData fin) {
				if (outerName == null) {
					outer.anon(t, new Annotation(t, fin));
				} else {
					outer.add(name, new AnnotationValue(t, new Annotation(t, fin)));
				}
			}
		};

	}

	/* package */ Map<String, AnnotationValue> fin = new HashMap<String, AnnotationValue>();

	@Override
	public final AnnotationVisitor visitArray(String name) {
		if (name == null || name.isEmpty())
			throw new Error();

		InternalAnnotationBuilder outer = this;

		return new InternalAnnotationBuilder() {
			@Override
			public void visitEnd() {
				if (anon == null)
					throw new Error();
				if (fin.size() != 0)
					throw new Error();

				if (anonType == null && !anon.isEmpty())
					throw new Error("Array annotation with elements, but without type");

				if (anonType == null)
					outer.add(name, new AnnotationValue(null, anon));
				else
					outer.add(name, new AnnotationValue(TypeUtil.toArray(anonType), anon));
			}
		};
	}

	/* package */ void add(String name, AnnotationValue ann) {
		Object old = fin.putIfAbsent(name, ann);
		if (old != null)
			throw new Error();
	}
}
