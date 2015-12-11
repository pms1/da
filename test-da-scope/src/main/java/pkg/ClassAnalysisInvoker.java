package pkg;

import org.objectweb.asm.ClassReader;

public interface ClassAnalysisInvoker {

	void run(ClassReader reader);

}
