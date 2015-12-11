package pkg;

import org.objectweb.asm.ClassReader;

public interface ClassAnalysis<T> extends Analysis<T> {

	void run(T config, ClassReader v);

}
