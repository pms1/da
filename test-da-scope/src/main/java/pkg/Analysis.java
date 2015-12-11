package pkg;

public interface Analysis<T> {
	void accept(AnalysisVisitor av);
}
