package com.github.da;

import javax.inject.Inject;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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
			}

			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				if (!visible)
					return null;

				Type t = Type.getType(desc);
				String cls = t.getClassName();
				String pkg = cls.substring(0, cls.lastIndexOf('.'));

				if (!config.packages.contains(pkg))
					return null;

				return new AV1() {
					public void visitEnd() {
						super.visitEnd();
						am.add(t, fin);
					};
				};
			}
		}, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
	}

}
