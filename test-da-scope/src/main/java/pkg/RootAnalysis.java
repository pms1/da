package pkg;

import java.io.IOException;

public interface RootAnalysis<T> extends Analysis {

	void run(T t, Processors p) throws IOException;

}
