package utils;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeUtils {
	public static <T> Type resolve(Type t, TypeVariable<Class<T>> typeVariable) {
		return TypeVisitor.accept(t, new TypeVisitor<Type>() {
			@Override
			public <T1> Type visit(Class<T1> v) {
				return resolveInternal(v, typeVariable, v.getTypeParameters());
			}

			@Override
			public Type visit(ParameterizedType v) {
				return resolveInternal(v, typeVariable, new Type[0]);
			}
		});
	}

	private static <T> Type resolveInternal(Type t, TypeVariable<Class<T>> typeVariable, Type[] assignments) {
		if (t == null)
			return null;

		if (t == typeVariable.getGenericDeclaration()) {

			Class<T> c = (Class) t;

			for (int i = c.getTypeParameters().length; i-- > 0;)
				if (c.getTypeParameters()[i] == typeVariable)
					return assignments[i];
			throw new Error();
		}

		return TypeVisitor.accept(t, new TypeVisitor<Type>() {

			Type tryParent(Class<?> v, Type parent) {
				if (parent == null)
					return null;

				return TypeVisitor.accept(parent, new TypeVisitor<Type>() {

					public <T1> Type visit(Class<T1> v) {
						return resolve(v, typeVariable);
					};

					@Override
					public Type visit(ParameterizedType v1) {
						Type[] resolved = new Type[v1.getActualTypeArguments().length];
						for (int i = v1.getActualTypeArguments().length; i-- > 0;) {
							Type t = v1.getActualTypeArguments()[i];

							resolved[i] = TypeVisitor.accept(t, new TypeVisitor<Type>() {
								@Override
								public <D extends GenericDeclaration> Type visit(TypeVariable<D> v2) {
									for (int i = v.getTypeParameters().length; i-- > 0;) {
										if (v.getTypeParameters()[i] == t) {
											return assignments[i];
										}
									}
									throw new Error();
								}

								@Override
								public <T1> Type visit(Class<T1> v) {
									return v;
								}

								@Override
								public Type visit(ParameterizedType v) {
									return v;
								}

							});
						}

						return resolveInternal(v1.getRawType(), typeVariable, resolved);
					}
				});
			}

			@Override
			public <T1> Type visit(Class<T1> v) {

				if (v.getTypeParameters().length != assignments.length)
					throw new Error();

				Type r = tryParent(v, v.getGenericSuperclass());
				if (r != null)
					return r;

				for (Type i : v.getGenericInterfaces()) {
					r = tryParent(v, i);
					if (r != null)
						return r;
				}

				return null;
			}

			@Override
			public Type visit(ParameterizedType v) {
				return resolveInternal(v.getRawType(), typeVariable, v.getActualTypeArguments());
			}
		});
	}

	public static Type getRawType(Type resolve) {
		return TypeVisitor.accept(resolve, new TypeVisitor<Class<?>>() {
			@Override
			public <T> Class<?> visit(Class<T> v) {
				return v;
			}

			@Override
			public Class<?> visit(ParameterizedType v) {
				return (Class<?>) v.getRawType();
			}
		});
	}
}
