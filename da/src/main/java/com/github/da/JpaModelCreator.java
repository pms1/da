package com.github.da;

import javax.inject.Inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import com.github.pms1.c4.classes.annotations.AnnotationModel;

public class JpaModelCreator implements ClassAnalysis<JpaModelCreatorConfig> {

	@Inject
	AnalysisResult ar;

	@Override
	public void run(JpaModelCreatorConfig config, ClassReader v) {
		ClassHierarchy2 ch = ar.get(ClassHierarchy2.class);

		v.accept(new ClassVisitor(Opcodes.ASM5) {

			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				AnnotationModel am = ch.get(AsmIds.forClass(name)).get(AnnotationModel.class);

			}
		}, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
	}

}
