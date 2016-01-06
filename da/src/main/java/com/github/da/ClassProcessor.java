package com.github.da;

import org.objectweb.asm.ClassReader;

@Analysis
public interface ClassProcessor {

	void run(ClasspathUnit cu, ClassReader v);

}
