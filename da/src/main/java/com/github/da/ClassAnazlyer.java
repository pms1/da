package com.github.da;

import org.objectweb.asm.ClassReader;

public interface ClassAnazlyer extends Phase2 {

	void analyse(ClassReader reader, ClassModel bean);

}
