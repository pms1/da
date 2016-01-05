package com.github.da.t;

import org.objectweb.asm.ClassReader;

@Analysis
public interface ClassProcessor {

	void run(ClassReader v);

}
