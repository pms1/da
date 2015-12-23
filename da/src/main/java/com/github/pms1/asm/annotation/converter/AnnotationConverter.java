package com.github.pms1.asm.annotation.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import com.github.pms1.asm.annotation.Annotation;
import com.github.pms1.asm.annotation.AnnotationData;
import com.github.pms1.asm.annotation.AnnotationValue;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;

public class AnnotationConverter {
	private static interface Model<T> {
		T create(AnnotationData fin);
	}

	private static class FieldsModel<T> implements Model<T> {
		static class Element {
			Function<AnnotationValue, Object> parser;
			Field field;
		}

		Class<T> clazz;
		Map<String, Element> fields = new HashMap<>();

		public T create(AnnotationData fin) {
			try {
				T result = clazz.newInstance();

				for (Map.Entry<String, AnnotationValue> e : fin.getData().entrySet()) {
					Element property = fields.get(e.getKey());
					if (property == null) {
						throw new RuntimeException(
								"No target for property '" + e.getKey() + "' found in " + clazz.getSimpleName());
					}
					property.field.set(result, property.parser.apply(e.getValue()));
				}

				return result;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

	}

	private static class ConstructorModel<T> implements Model<T> {
		Constructor<T> c;

		static class Element {
			Function<AnnotationValue, Object> parser;
			int idx;
		}

		Map<String, Element> fields = new HashMap<>();

		@Override
		public T create(AnnotationData fin) {
			Object[] args = new Object[c.getParameterCount()];

			for (Map.Entry<String, AnnotationValue> e : fin.getData().entrySet()) {
				Element property = fields.get(e.getKey());
				if (property == null) {
					throw new RuntimeException(
							"No target for property '" + e.getKey() + "' found in " + c.getDeclaringClass());
				}
				args[property.idx] = property.parser.apply(e.getValue());
			}

			try {
				return (T) c.newInstance(args);
			} catch (ReflectiveOperationException e1) {
				throw new RuntimeException(e1);
			}
		}

	}

	private static final LoadingCache<Class<?>, Model<?>> models = CacheBuilder.newBuilder() //
			.maximumSize(500) //
			.build(new CacheLoader<Class<?>, Model<?>>() {
				TypeToken<?> enumToken = TypeToken.of(Enum.class);

				Function<Object, Object> findParser(java.lang.reflect.Type t) throws Exception {
					Objects.requireNonNull(t);

					if (t.equals(String.class)) {
						return (p) -> (String) p;
					} else if (t.equals(Boolean.class)) {
						return (p) -> (Boolean) p;
					} else if (t.equals(Integer.class)) {
						return (p) -> (Integer) p;
					} else if (enumToken.isAssignableFrom(t)) {
						Method m1 = ((Class<?>) t).getMethod("valueOf", String.class);
						return (p) -> {
							try {
								return m1.invoke(null, (String) p);
							} catch (ReflectiveOperationException e1) {
								throw new RuntimeException(e1);
							}
						};
					}

					TypeToken<?> tt = TypeToken.of(t);

					if (tt.getRawType().equals(List.class)) {
						TypeToken<?> resolveType = tt.resolveType(List.class.getTypeParameters()[0]);
						Class<?> raw = resolveType.getRawType();
						Function<Object, Object> parser = findParser(raw);
						return (p) -> {
							List<Object> l = new LinkedList<>();
							for (Object o : (List<?>) p)
								l.add(parser.apply(o));
							return l;
						};

					} else {
						Class<?> c = (Class<?>) t;
						return (v) -> convertAnnotation(c, ((Annotation) v).getData());
					}
				}

				@Override
				public Model<?> load(Class<?> key) throws Exception {
					return load1(key);
				}

				public <T> Model<T> load1(Class<T> key) throws Exception {
					Constructor<?>[] constructors = key.getConstructors();
					if (constructors.length != 1)
						throw new RuntimeException("k=" + key + " " + Arrays.toString(constructors));

					@SuppressWarnings("unchecked")
					Constructor<T> c = (Constructor<T>) constructors[0];
					Parameter[] params = c.getParameters();
					if (params.length != 0) {
						ConstructorModel<T> m = new ConstructorModel<T>();
						m.c = c;
						int idx = 0;
						for (Parameter p : params) {
							if (!p.isNamePresent())
								throw new Error();

							findParser(p.getParameterizedType());

							ConstructorModel.Element e = new ConstructorModel.Element();
							e.idx = idx;
							Function<Object, Object> parser = findParser(p.getParameterizedType());
							e.parser = (v) -> parser.apply(v.getValue());

							m.fields.put(p.getName(), e);

							++idx;

						}
						return m;
					} else {
						FieldsModel<T> m = new FieldsModel<T>();
						m.clazz = key;
						m.fields = new HashMap<>();
						for (Field f : key.getFields()) {
							FieldsModel.Element e = new FieldsModel.Element();
							e.field = f;
							m.fields.put(f.getName(), e);
							Function<Object, Object> parser = findParser(f.getGenericType());
							e.parser = (v) -> parser.apply(v.getValue());
						}

						return m;
					}
				}

			});

	public static <T> T convertAnnotation(Class<T> clazz, AnnotationData fin) {
		try {
			@SuppressWarnings("unchecked")
			Model<T> m = (Model<T>) models.get(clazz);

			return m.create(fin);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

	}

}
