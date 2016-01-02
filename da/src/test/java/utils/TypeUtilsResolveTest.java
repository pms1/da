package utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.util.TypeLiteral;

import org.junit.Test;

public class TypeUtilsResolveTest {
	static class C1 extends HashMap<String, Integer> {

	}

	static class C21<A, B> extends HashMap<B, A> {

	}

	static class C22 extends C21<String, Integer> {

	}

	static class C3 extends HashMap {

	}

	static class C4 extends HashMap<String, List<String>> {

	}

	@Test
	public void basic() {
		assertThat(TypeUtils.resolve(C1.class, Map.class.getTypeParameters()[0]), equalTo(String.class));
		assertThat(TypeUtils.resolve(C1.class, Map.class.getTypeParameters()[1]), equalTo(Integer.class));
		assertThat(TypeUtils.resolve(C1.class, AbstractMap.class.getTypeParameters()[0]), equalTo(String.class));
		assertThat(TypeUtils.resolve(C1.class, AbstractMap.class.getTypeParameters()[1]), equalTo(Integer.class));
	}

	@Test
	public void swapped() {
		assertThat(TypeUtils.resolve(C22.class, Map.class.getTypeParameters()[0]), equalTo(Integer.class));
		assertThat(TypeUtils.resolve(C22.class, Map.class.getTypeParameters()[1]), equalTo(String.class));
		assertThat(TypeUtils.resolve(C22.class, AbstractMap.class.getTypeParameters()[0]), equalTo(Integer.class));
		assertThat(TypeUtils.resolve(C22.class, AbstractMap.class.getTypeParameters()[1]), equalTo(String.class));
	}

	@Test
	public void parameters() {
		assertThat(TypeUtils.resolve(C21.class, Map.class.getTypeParameters()[0]),
				equalTo(C21.class.getTypeParameters()[1]));
		assertThat(TypeUtils.resolve(C21.class, Map.class.getTypeParameters()[1]),
				equalTo(C21.class.getTypeParameters()[0]));
		assertThat(TypeUtils.resolve(C21.class, AbstractMap.class.getTypeParameters()[0]),
				equalTo(C21.class.getTypeParameters()[1]));
		assertThat(TypeUtils.resolve(C21.class, AbstractMap.class.getTypeParameters()[1]),
				equalTo(C21.class.getTypeParameters()[0]));
	}

	private static final Type listOfStringType = new TypeLiteral<List<String>>() {
	}.getType();

	@Test
	public void nested() {
		assertThat(TypeUtils.resolve(C4.class, Map.class.getTypeParameters()[1]), equalTo(listOfStringType));
	}

	@Test
	public void nonGeneric() {
		assertThat(TypeUtils.resolve(C3.class, Map.class.getTypeParameters()[0]),
				equalTo(HashMap.class.getTypeParameters()[0]));
		assertThat(TypeUtils.resolve(C3.class, Map.class.getTypeParameters()[1]),
				equalTo(HashMap.class.getTypeParameters()[1]));
	}
}
