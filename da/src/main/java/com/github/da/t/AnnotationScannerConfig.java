package com.github.da.t;

public class AnnotationScannerConfig extends AnalyserConfiguration<AnnotationScanner> {
	final int what;
	final int many;

	AnnotationScannerConfig(int what, int many) {
		super(AnnotationScanner.class);
		this.what = what;
		this.many = many;
	}

	@Override
	public String toString() {
		return super.toString() + "(what=" + what + ",many=" + many + ")";
	}
}
