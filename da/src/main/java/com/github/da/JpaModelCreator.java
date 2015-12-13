package com.github.da;

import javax.inject.Inject;

import org.objectweb.asm.ClassReader;

@Require(ClassHierarchy.class)
public class JpaModelCreator implements ClassAnalysis<JpaModelCreatorConfig> {

	@Inject
	AnalysisResult ar;
	
	@Inject
	ClassHierarchy ch;
	
	@Override
	public void run(JpaModelCreatorConfig config, ClassReader v) {
		// TODO Auto-generated method stub
		System.out.println("JPA ANA " + ar + " " + ch);
	}

}
