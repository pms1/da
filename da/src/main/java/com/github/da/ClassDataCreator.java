package com.github.da;

import javax.inject.Inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.da.ClassDataCreatorConfig.Data;

@Include(ClassProcessor2.class)
public class ClassDataCreator implements ClassAnalysis<ClassDataCreatorConfig> {

	@Inject
	AnalysisResult ar;

	@Override
	public void run(ClassDataCreatorConfig config, ClassReader v) {
		assert config == null;

		ClassHierarchy2 ch = ar.getOrCreate(ClassHierarchy2.class, ClassHierarchy2::new);

		boolean doClass = config.datas.contains(Data.CLASS);
		boolean doMethod = config.datas.contains(Data.METHOD);
		boolean doField = config.datas.contains(Data.FIELD);

		v.accept(new ClassVisitor(Opcodes.ASM5) {
			ClassData classData;

			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				if (doClass) {
					ClassId classId = AsmIds.forClass(name);
					// FIXME
					ch.remove(classId);
					ch.put(classId, classData = new ClassData());
				}
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature,
					String[] exceptions) {
				if (doMethod)
					classData.put(AsmIds.forMethod(name, desc), new MethodData());

				return super.visitMethod(access, name, desc, signature, exceptions);
			}

			@Override
			public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
				if (doField)
					classData.put(AsmIds.forField(name), new FieldData());

				return super.visitField(access, name, desc, signature, value);
			}

		}, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
	}

}
