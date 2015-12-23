package com.github.pms1.asm.annotation;

import org.objectweb.asm.AnnotationVisitor;

/**
 * An {@link AnnotationVisitor} that builds an {@link AnnotationData} object
 * from the visited annotation.
 * 
 * @author pms1
 *
 */
public abstract class AnnotationBuilder extends InternalAnnotationBuilder {

	@Override
	public final void visitEnd() {
		visitEnd(AnnotationData.of(fin));
	}

	public abstract void visitEnd(AnnotationData data);
}
