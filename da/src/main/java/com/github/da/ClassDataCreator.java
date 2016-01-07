package com.github.da;

import javax.inject.Inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.da.ClassDataCreatorConfig.Data;

import asm.ClassSignature;
import ts.AsmTypeParser;

@Include(ClassResourceProcessor.class)
public class ClassDataCreator implements ClassProcessor {

	@Inject
	AnalysisResult ar;

	@Inject
	ClassDataCreatorConfig config;

	@Override
	public void run(Archive archive, ResourceId id, ClassReader classReader) {
		ClassHierarchy ch = archive.getOrCreate(ClassHierarchy.class, ClassHierarchy::new);

		boolean doClassType = config.datas.contains(Data.CLASS_TYPE);
		boolean doClassSignature = config.datas.contains(Data.CLASS_SIGNATURE);
		boolean doMethod = config.datas.contains(Data.METHOD);
		boolean doField = config.datas.contains(Data.FIELD);
		boolean doClass = doClassType || doClassSignature || doField || doMethod || config.datas.contains(Data.CLASS);

		classReader.accept(new ClassVisitor(Opcodes.ASM5) {
			ClassData classData;

			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				if (doClass) {
					ClassId classId = AsmIds.forClass(name);
					// FIXME
					ch.remove(classId);
					ch.put(classId, classData = new ClassData(classId));
					archive.put(id, ClassData.class, classData);

					if (doClassType) {
						classData.put(Type.class, Type.getObjectType(name));
					}

					if (doClassSignature) {
						classData.put(ClassSignature.class,
								AsmTypeParser.parseClassSignature(name, signature, superName, interfaces));
					}
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

	@Override
	public String toString() {
		return super.toString() + "(" + config + ")";
	}
}
