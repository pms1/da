package pkg;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.inject.Provider;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ClassProcessor implements JarContentProcessor<Void> {
	static class CV1 extends ClassVisitor {
		// ClassModel bean;

		public CV1() {
			super(Opcodes.ASM5);
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {

			// Type type = Type.getObjectType(name);
			//
			// ClassSignature classSignature =
			// AsmTypeParser.parseClassSignature(name, signature, superName,
			// interfaces);
			// Type superType = Type.getObjectType(superName);
			// List<Type> xinterfaces = new ArrayList<>(interfaces.length);
			// for (String inter : interfaces)
			// xinterfaces.add(Type.getObjectType(inter));
			// xinterfaces = Collections.unmodifiableList(xinterfaces);
			//
			// bean = new ClassModel(type, classSignature.getSuperclass(),
			// xinterfaces);

			super.visit(version, access, name, signature, superName, interfaces);
		}
	}

	@Override
	public void accept(AnalysisVisitor av) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(Void config, Processors proc, Path p, Provider<InputStream> is) throws IOException {
		if (!p.toString().endsWith(".class"))
			return;

		ClassReader reader = new ClassReader(is.get());

		CV1 v = new CV1();
		reader.accept(v, 0);

		for (ClassAnalysisInvoker ca : proc.classAnalyes) {
			ca.run(reader);
		}

	}
}
