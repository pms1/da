package com.github.da;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import asm.ClassSignature;
import ts.AsmTypeParser;

public class ClassProcessor {

	static class CV1 extends ClassVisitor {
		ClassModel bean;

		public CV1() {
			super(Opcodes.ASM5);
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {

			Type type = Type.getObjectType(name);

			ClassSignature classSignature = AsmTypeParser.parseClassSignature(name, signature, superName, interfaces);
			Type superType = Type.getObjectType(superName);
			List<Type> xinterfaces = new ArrayList<>(interfaces.length);
			for (String inter : interfaces)
				xinterfaces.add(Type.getObjectType(inter));
			xinterfaces = Collections.unmodifiableList(xinterfaces);

			bean = new ClassModel(type, classSignature.getSuperclass(), xinterfaces);

			super.visit(version, access, name, signature, superName, interfaces);
		}
	}

	static Map<Type, ClassModel> classes = new HashMap<>();

	@Inject
	@Any
	Instance<ClassAnazlyer> analysers;

	public void process(InputStream is) throws IOException {

		ClassReader reader = new ClassReader(is);

		CV1 v = new CV1();
		reader.accept(v, 0);

		ClassModel old = classes.putIfAbsent(v.bean.type, v.bean);
		if (old != null)
			System.err.println("WARNING: duplicate " + v.bean.type);

		for (ClassAnazlyer a : analysers)
			a.analyse(reader, v.bean);
	}

}
