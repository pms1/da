package pkg;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class A2 implements PostAnalysis {
	@Inject
	AnaResult aresult;

	@Override
	public void run() {
		Data1 data1 = aresult.get(Data1.class);
		System.err.println("A2 DOING WITH RESULT " + data1);
	}
}
