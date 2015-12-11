package pkg;

import java.util.Arrays;
import java.util.List;

@AnaScope
public class A3Config {

	final List<Class<?>> classes;

	public A3Config(Class<?>... classes) {
		this.classes = Arrays.asList(classes);
	}

}
