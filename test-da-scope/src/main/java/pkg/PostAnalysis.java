package pkg;

public interface PostAnalysis extends Analysis {
	void run();

	default void accept(AnalysisVisitor av) {
		av.visit(this);
	}
}
