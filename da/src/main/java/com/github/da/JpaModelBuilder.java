package com.github.da;

import java.util.Map;
import java.util.Objects;

import org.objectweb.asm.Type;

public class JpaModelBuilder {

	void doit(ClassHierarchy ch) {
		for (Map.Entry<Type, ClassModel> e : ClassProcessor.classes.entrySet()) {

			JpaAnalysisResult jpaAnalysisResult = e.getValue().get(JpaAnalysisResult.class);
			if (jpaAnalysisResult == null)
				continue;
			if (jpaAnalysisResult.isEntity())
				continue;

			System.err.println("E " + e.getKey());

			traverse("  ", jpaAnalysisResult);
		}

	}

	static void traverse(String prefix, JpaAnalysisResult c) {
		Objects.requireNonNull(c);

		if (false)
			if (!c.clazz.getSuperType().getRawType().equals(JpaClassAnalyser.javaLangObject)) {
				System.err.println(" -> " + c.clazz.getSuperType());
				JpaAnalysisResult superC = ClassProcessor.classes.get(c.clazz.getSuperType().getRawType())
						.get(JpaAnalysisResult.class);
				if (superC != null)
					traverse(prefix, superC);
			}
		for (JpaProperty p : c.properties.values()) {
			traverse(prefix, p);
		}
	}

	static void traverse(String prefix, JpaProperty p) {
		System.err.println(prefix + p.name + ": " + p.type);

		switch (p.fieldType) {
		case VALUE:
			System.err.println(prefix + "  COL " + p.column);
			break;
		case EMBEDDED:
			JpaAnalysisResult result = ClassProcessor.classes.get(p.type).get(JpaAnalysisResult.class);
			// traverse(prefix + " ", result);
			break;
		default:
			System.err.println(prefix + "  " + p.fieldType);
			break;
		}
	}
}
