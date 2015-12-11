package pkg;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Produce(Data1.class)
public class A1 implements PostAnalysis {
	@Inject
	AnaResult aresult;

	@Override
	public void run() {
		aresult.put(Data1.class, new Data1("from " + this));
	}
}
