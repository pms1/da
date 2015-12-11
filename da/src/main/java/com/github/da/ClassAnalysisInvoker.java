package com.github.da;

import org.objectweb.asm.ClassReader;

public interface ClassAnalysisInvoker {

	void run(ClassReader reader);

}
