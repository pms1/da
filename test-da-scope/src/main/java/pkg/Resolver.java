package pkg;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Dependent
public class Resolver {
	private @Inject Instance<Object> i;

	public <T> T resolve(Class<T> t) {
		return (T) i.select(t).get();
	}

}
