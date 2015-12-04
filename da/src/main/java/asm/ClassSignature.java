package asm;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

public class ClassSignature {

	final List<TypeParameter> parameters;

	final ClassType superclass;

	final List<ClassType> interfaces;

	public ClassSignature(List<TypeParameter> parameters, ClassType superclass, List<ClassType> interfaces) {
		Objects.requireNonNull(parameters);
		this.parameters = parameters;
		Objects.requireNonNull(superclass);
		this.superclass = superclass;
		Objects.requireNonNull(interfaces);
		this.interfaces = interfaces;
	}

	@Override
	public String toString() {
		return "ClassSignature " + parameters + " " + superclass + " " + interfaces;
	}

	public String asJava() {
		StringBuilder b = new StringBuilder();
		if (parameters.size() != 0) {
			b.append("<");
			Joiner.on(",").appendTo(b, Collections2.transform(parameters, new Function<TypeParameter, String>() {

				@Override
				public String apply(TypeParameter input) {
					return input.asJava();
				}

			}));
			Joiner.on(",").appendTo(b, Collections2.transform(parameters, (p) -> p.asJava()));
			b.append(">");
		}
		b.append(" extends ").append(superclass.asJava());
		if (interfaces.size() != 0) {
			b.append(" implements ");
			Joiner.on(",").appendTo(b, Collections2.transform(interfaces, new Function<JavaType, String>() {

				@Override
				public String apply(JavaType input) {
					return input.asJava();
				}

			}));
		}
		return b.toString();
	}

	/**
	 * @return the parameters
	 */
	public List<TypeParameter> getParameters() {
		return parameters;
	}

	/**
	 * @return the superclass
	 */
	public ClassType getSuperclass() {
		return superclass;
	}

	/**
	 * @return the interfaces
	 */
	public List<ClassType> getInterfaces() {
		return interfaces;
	}

}