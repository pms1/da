package com.github.da;

import javax.inject.Inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

@Provide(ClassHierarchy2.class)
@Include(ClassProcessor2.class)
public class ClassHierarchyCreator implements ClassAnalysis<Void> {

	@Inject
	AnalysisResult ar;

	@Override
	public void run(Void config, ClassReader v) {
		assert config == null;

		ClassHierarchy2 ch = ar.getOrCreate(ClassHierarchy2.class, ClassHierarchy2::new);

		v.accept(new ClassVisitor(Opcodes.ASM5) {
			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				ClassId classId = AsmIds.forClass(name);
				ch.remove(classId);
				ch.put(classId, new ClassData());
			}

		}, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
	}

}
