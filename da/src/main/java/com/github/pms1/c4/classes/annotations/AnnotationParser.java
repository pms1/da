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
import com.github.da.ClassAnalysis;
import com.github.da.ClassHierarchy2;
import com.github.pms1.asm.annotation.AnnotationBuilder;
import com.github.pms1.asm.annotation.AnnotationData;

public class AnnotationParser implements ClassAnalysis<AnnotationParserConfig> {

	@Inject
	AnalysisResult ar;

	@Override
	public void run(AnnotationParserConfig config, ClassReader v) {
		ClassHierarchy2 ch = ar.get(ClassHierarchy2.class);

		v.accept(new ClassVisitor(Opcodes.ASM5) {

			AnnotationModel am;

			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				am = new AnnotationModel();
				ch.get(AsmIds.forClass(name)).put(AnnotationModel.class, am);
				System.err.println("CH " + name);
			}

			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				if (!visible)
					return null;

				Type t = Type.getType(desc);
				String cls = t.getClassName();
				String pkg = cls.substring(0, cls.lastIndexOf('.'));

				if (false)
					if (!config.packages.contains(pkg))
						return null;

				return new AnnotationBuilder() {
					public void visitEnd(AnnotationData data) {
						am.add(t, data);
						System.err.println("AC " + data);
					};
				};
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature,
					String[] exceptions) {

				return new MethodVisitor(Opcodes.ASM5) {
					@Override
					public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
						return new AnnotationBuilder() {

							@Override
							public void visitEnd(AnnotationData data) {
								System.err.println("AM " + data);

							}

						};
					}
				};
			}

			@Override
			public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
				return new FieldVisitor(Opcodes.ASM5) {
					@Override
					public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
						return new AnnotationBuilder() {

							@Override
							public void visitEnd(AnnotationData data) {
								System.err.println("AF " + data);

							}

						};
					}
				};
			}

		}, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
	}

}
