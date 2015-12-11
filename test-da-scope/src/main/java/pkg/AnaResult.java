package pkg;

import java.util.HashMap;
import java.util.Map;

@AnaScope
public class AnaResult {
	String s;

	AnaResult(String s) {
		this.s = s;
	}

	@Override
	public String toString() {
		return super.toString() + " " + s;
	}

	private Map<Class<?>, Object> data = new HashMap<>();

	public <T> void put(Class<T> class1, T data1) {
		data.put(class1, data1);

	}

	public <T> T get(Class<T> class1) {
		T result = (T) data.get(class1);
		if (result == null)
			throw new IllegalArgumentException();
		return result;
	}
}
