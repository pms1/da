package com.github.da.t;

public class AnnotationScannerConfig extends AnalyserConfiguration<AnnotationScanner> {
	final int what;

	AnnotationScannerConfig(int what) {
		super(AnnotationScanner.class);
		this.what = what;
	}

	@Override
	public String toString() {
		return super.toString() + "(what=" + what + ")";
	}
}
