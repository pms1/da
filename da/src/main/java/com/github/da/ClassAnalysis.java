package com.github.da;

import org.objectweb.asm.ClassReader;

public interface ClassAnalysis<T> extends Analyser<T> {

	void run(T config, ClassReader v);

}
