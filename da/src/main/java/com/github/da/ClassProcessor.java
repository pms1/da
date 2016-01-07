package com.github.da;

import org.objectweb.asm.ClassReader;

@Analysis
public interface ClassProcessor {

	void run(Archive archive, ResourceId id, ClassReader classReader);

}
