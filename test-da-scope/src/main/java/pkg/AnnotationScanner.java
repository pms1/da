package pkg;

import javax.inject.Inject;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class AnnotationScanner implements ClassAnalysis<AnnotationScannerConfig> {

	@Override
	public void accept(AnalysisVisitor av) {
		// TODO Auto-generated method stub

	}

	@Inject
	AnaResult ar;

	@Override
	public void run(AnnotationScannerConfig config, ClassReader v) {

		v.accept(new ClassVisitor(Opcodes.ASM5) {
			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				System.err.println("D " + desc + " " + ar);
				return null;
			}
		}, 0);
	}

}
