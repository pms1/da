package com.github.da;

import org.objectweb.asm.ClassReader;

@Analysis
public interface ClassProcessor {

	void run(Archive cu, ClassReader v);

}
