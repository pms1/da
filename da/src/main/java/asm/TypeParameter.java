package asm;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

public class TypeParameter {

	public String name;

	public JavaType classBound;

	public List<JavaType> interfaceBound = new LinkedList<>();

	@Override
	public String toString() {
		return "TypeParameter " + name + " " + classBound + " " + interfaceBound;
	}

	public String asJava() {
		StringBuilder b = new StringBuilder();
		b.append(name);
		if (classBound != null || interfaceBound.size() != 0)
			b.append(" extends");
		if (classBound != null)
			b.append(" ").append(classBound.asJava());
		if (interfaceBound.size() != 0) {
			if (classBound != null)
				b.append("&");
			else
				b.append(" ");
			Joiner.on(",").appendTo(b, Collections2.transform(interfaceBound, new Function<JavaType, String>() {

				@Override
				public String apply(JavaType input) {
					return input.asJava();
				}

			}));
		}
		return b.toString();
	}
}