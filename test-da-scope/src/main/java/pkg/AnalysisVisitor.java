package pkg;

public interface AnalysisVisitor {
	void visit(PostAnalysis pa);

	void visit(JarContentProcessor jarContentProcessor);
}
