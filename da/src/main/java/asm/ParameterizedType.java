package asm;

import java.util.List;
import java.util.Objects;

import org.objectweb.asm.Type;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

public class ParameterizedType extends ClassType {

	private final Type name;

	private final List<TypeArgument> typeArguments;

	public List<TypeArgument> getTypeArguments() {
		return typeArguments;
	}

	private final List<InnerClass> innerClasses;

	public ParameterizedType(Type name, List<TypeArgument> typeArguments, List<InnerClass> innerClasses) {
		Objects.requireNonNull(name);
		this.name = name;
		Objects.requireNonNull(typeArguments);
		this.typeArguments = typeArguments;
		Objects.requireNonNull(innerClasses);
		this.innerClasses = innerClasses;
	}

	@Override
	public String toString() {
		return "ParameterizedType(" + name + " " + typeArguments + " " + innerClasses + ")";
	}

	@Override
	public String asJava() {
		StringBuilder b = new StringBuilder();
		b.append(name.getClassName());
		if (typeArguments.size() != 0) {
			b.append("<");
			Joiner.on(",").appendTo(b, Collections2.transform(typeArguments, new Function<TypeArgument, String>() {

				@Override
				public String apply(TypeArgument input) {
					return input.asJava();
				}

			}));
			b.append(">");
		}
		if (innerClasses.size() != 0) {
			b.append(".");
			Joiner.on(".").appendTo(b, Collections2.transform(innerClasses, new Function<InnerClass, String>() {

				@Override
				public String apply(InnerClass input) {
					return input.asJava();
				}

			}));
		}
		return b.toString();

	}

	/**
	 * @return the name
	 */
	public Type getName() {
		return name;
	}

	/**
	 * @return the innerClasses
	 */
	public List<InnerClass> getInnerClasses() {
		return innerClasses;
	}

	@Override
	public Type getRawType() {
		if (innerClasses.isEmpty())
			return name;

		StringBuffer b = new StringBuffer();
		b.append(name.getInternalName());
		for (InnerClass i : innerClasses) {
			b.append("$");
			b.append(i.name.getInternalName());
		}
		return Type.getObjectType(b.toString());
	}

	@Override
	public <T> T accept(JavaTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}