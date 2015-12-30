package pkg;

import javax.inject.Inject;

public class T1 {
	@Inject
	T2 t1;

	@Inject
	void t3(T3 t3, T4 t4) {

	}
}
