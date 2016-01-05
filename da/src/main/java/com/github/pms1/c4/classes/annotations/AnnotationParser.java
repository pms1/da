package com.github.pms1.c4.classes.annotations;

import javax.inject.Inject;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.da.AnalysisResult;
import com.github.da.AsmIds;
import com.github.da.ClassData;
import com.github.da.ClassHierarchy;
import com.github.pms1.asm.annotation.AnnotationBuilder;
import com.github.pms1.asm.annotation.AnnotationData;

public class AnnotationParser implements com.github.da.t.ClassProcessor {

	@Inject
	AnalysisResult ar;

	@Inject
	AnnotationParserConfig config;

	@Override
	public void run(ClassReader v) {
		ClassHierarchy ch = ar.get(ClassHierarchy.class);

		v.accept(new ClassVisitor(Opcodes.ASM5) {

			AnnotationModel classAnotationModel;
			private ClassData classData;

			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				classAnotationModel = new AnnotationModel();
				classData = ch.get(AsmIds.forClass(name));
				classData.put(AnnotationModel.class, classAnotationModel);
			}

			boolean parse(Type t) {
				String cls = t.getClassName();
				String pkg = cls.substring(0, cls.lastIndexOf('.'));

				return config.packages.contains(pkg);
			}

			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				if (!visible)
					return null;

				Type t = Type.getType(desc);
				if (!parse(t))
					return null;

				return new AnnotationBuilder() {
					public void visitEnd(AnnotationData data) {
						classAnotationModel.add(t, data);
					};
				};
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature,
					String[] exceptions) {

				AnnotationModel methodAnnotationModel = new AnnotationModel();

				classData.get(AsmIds.forMethod(name, desc)).put(AnnotationModel.class, methodAnnotationModel);

				return new MethodVisitor(Opcodes.ASM5) {
					@Override
					public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
						Type t = Type.getType(desc);
						if (!parse(t))
							return null;

						return new AnnotationBuilder() {
							public void visitEnd(AnnotationData data) {
								methodAnnotationModel.add(t, data);
							};
						};
					}
				};
			}

			@Override
			public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {

				AnnotationModel fieldAnnotationModel = new AnnotationModel();

				classData.get(AsmIds.forField(name)).put(AnnotationModel.class, fieldAnnotationModel);

				return new FieldVisitor(Opcodes.ASM5) {
					@Override
					public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
						Type t = Type.getType(desc);
						if (!parse(t))
							return null;

						return new AnnotationBuilder() {
							public void visitEnd(AnnotationData data) {
								fieldAnnotationModel.add(t, data);
							};
						};
					}
				};
			}

		}, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
	}

}
