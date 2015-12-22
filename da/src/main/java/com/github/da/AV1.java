package com.github.da;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class AV1 extends AnnotationVisitor {

	public AV1() {
		super(Opcodes.ASM5);
	}

	List<Object> anon = new LinkedList<>();
	Type anonType = null;

	void anon(Type type, Object value) {
		if (anonType == null)
			anonType = type;
		else if (!anonType.equals(type))
			throw new Error();

		anon.add(value);
	}

	@Override
	public void visit(String name, Object value) {
		if (value == null)
			throw new Error();

		Type type;
		if (value.getClass().equals(String.class)) {
			type = Type.getType(value.getClass());
		} else if (value.getClass().equals(Boolean.class)) {
			type = Type.getType(value.getClass());
		} else if (value.getClass().equals(Integer.class)) {
			type = Type.getType(value.getClass());
		} else if (value.getClass().equals(Long.class)) {
			type = Type.getType(value.getClass());
		} else if (value.getClass().equals(Type.class)) {
			type = Type.getType(Class.class);
		} else {
			throw new Error("name=" + name + " value=" + value + " " + value.getClass());
		}

		if (name == null) {
			anon(type, value);
		} else {
			add(new Ann(name, type, value));
		}
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		if (name == null) {
			anon(Type.getType(desc), value);
			return;
		}
		add(new Ann(name, Type.getType(desc), value));
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		String outerName = name;
		Type t = Type.getType(desc);
		AV1 outer = this;

		return new AV1() {
			@Override
			public void visitEnd() {
				if (outerName == null) {
					outer.anon(t, new Anno(t, fin));
				} else {
					outer.add(new Ann(name, t, new Anno(t, fin)));
				}
			}
		};

	}

	static class Anno {
		private final Type type;
		private final Map<String, Ann> ann;

		Anno(Type type, Map<String, Ann> ann) {
			this.type = type;
			this.ann = ann;
		}

		@Override
		public String toString() {
			return "Annotation(" + type + "," + ann.values() + ")";
		}
	}

	static class Ann {
		Ann(String name, Type type, Object value) {
			Objects.requireNonNull(name);
			this.name = name;
			Objects.requireNonNull(type);
			this.type = type;
			this.value = value;
		}

		Ann(String name) {
			Objects.requireNonNull(name);
			this.name = name;
			this.type = null;
			this.value = null;
		}

		final String name;
		final Type type;
		final Object value;

		@Override
		public String toString() {
			return name + "(" + type + ")=" + value;
		}
	}

	Map<String, Ann> fin = new HashMap<String, Ann>();

	@Override
	public AnnotationVisitor visitArray(String name) {
		if (name == null || name.isEmpty())
			throw new Error();

		AV1 outer = this;

		return new AV1() {
			@Override
			public void visitEnd() {
				if (anon == null)
					throw new Error();
				if (fin.size() != 0)
					throw new Error();

				if (anonType == null && anon.size() == 0)
					outer.add(new Ann(name));
				else
					outer.add(new Ann(name, TypeUtil.toArray(anonType), anon));
			}
		};
	}

	protected void add(Ann ann) {
		Object old = fin.putIfAbsent(ann.name, ann);
		if (old != null)
			throw new Error();
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}
}
