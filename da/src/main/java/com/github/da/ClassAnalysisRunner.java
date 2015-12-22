package com.github.da;

import org.objectweb.asm.ClassReader;

public interface ClassAnalysisRunner {

	void run(ClassReader reader);

}
