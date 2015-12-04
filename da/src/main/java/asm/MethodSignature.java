package asm;

import java.util.List;
import java.util.Objects;

public class MethodSignature {

	private final List<JavaType> exceptions;
	private final JavaType returnType;
	private final List<JavaType> parameters;
	private final List<TypeParameter> typeParameters;

	public MethodSignature(List<TypeParameter> typeParameters, List<JavaType> parameters, JavaType returnType,
			List<JavaType> exceptions) {
		Objects.requireNonNull(typeParameters);
		this.typeParameters = typeParameters;
		Objects.requireNonNull(parameters);
		this.parameters = parameters;
		this.returnType = returnType;
		Objects.requireNonNull(exceptions);
		this.exceptions = exceptions;
	}

	@Override
	public String toString() {
		return "MethodSignature " + typeParameters + " " + parameters + " " + returnType + " " + exceptions;
	}

	public JavaType getReturnType() {
		return returnType;
	}
}
