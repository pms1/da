package pkg;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class A3 implements PostAnalysis {
	@Inject
	Data1 data1;

	Object o;

	@Inject
	Resolver resolver;

	@Inject
	void setConfig(A3Config config) {
		for (Class<?> c : config.classes)
			o = resolver.resolve(c);
	}

	A3Config config;

	@Override
	public void run() {
		System.err.println("A3 DOING WITH RESULT " + data1 + " " + config + " " + o);
	}
}
