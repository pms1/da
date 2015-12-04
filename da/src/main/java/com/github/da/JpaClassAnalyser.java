package com.github.da;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.da.JpaClassAnalyser.AV1.Ann;
import com.github.da.JpaClassAnalyser.AV1.Anno;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;

import asm.JavaType;
import asm.JavaTypeVisitor;
import asm.MethodSignature;
import asm.ParameterizedType;
import asm.RawType;
import asm.TypeArgument;
import ts.AsmTypeParser;

public class JpaClassAnalyser implements ClassAnazlyer {

	// ( visit | visitEnum | visitAnnotation | visitArray )*
	// visitEnd
	static class AV1 extends AnnotationVisitor {

		public AV1() {
			super(Opcodes.ASM5);
		}

		List<Object> anon = new LinkedList<>();
		Type anonType = null;

		void anon(Type type, Object value) {
			if (anonType == null)
				anonType = type;
			else if (!anonType.equals(type))
				throw new Error();

			anon.add(value);
		}

		@Override
		public void visit(String name, Object value) {
			if (value == null)
				throw new Error();

			Type type;
			if (value.getClass().equals(String.class)) {
				type = Type.getType(value.getClass());
			} else if (value.getClass().equals(Boolean.class)) {
				type = Type.getType(value.getClass());
			} else if (value.getClass().equals(Integer.class)) {
				type = Type.getType(value.getClass());
			} else if (value.getClass().equals(Long.class)) {
				type = Type.getType(value.getClass());
			} else if (value.getClass().equals(Type.class)) {
				type = Type.getType(Class.class);
			} else {
				throw new Error("name=" + name + " value=" + value + " " + value.getClass());
			}

			if (name == null) {
				anon(type, value);
			} else {
				add(new Ann(name, type, value));
			}
		}

		@Override
		public void visitEnum(String name, String desc, String value) {
			if (name == null) {
				anon(Type.getType(desc), value);
				return;
			}
			add(new Ann(name, Type.getType(desc), value));
		}

		@Override
		public AnnotationVisitor visitAnnotation(String name, String desc) {
			String outerName = name;
			Type t = Type.getType(desc);
			AV1 outer = this;

			System.err.println("XXX VISIT ANN " + name + " " + desc);

			return new AV1() {
				@Override
				public void visitEnd() {
					System.err.println("XXX END1 " + this + " " + outer + " " + fin);
					if (outerName == null) {
						outer.anon(t, new Anno(t, fin));
					} else {
						outer.add(new Ann(name, t, new Anno(t, fin)));
					}
				}
			};

		}

		static class Anno {
			private final Type type;
			private final Map<String, Ann> ann;

			Anno(Type type, Map<String, Ann> ann) {
				this.type = type;
				this.ann = ann;
			}

			@Override
			public String toString() {
				return "Annotation(" + type + "," + ann.values() + ")";
			}
		}

		static class Ann {
			Ann(String name, Type type, Object value) {
				Objects.requireNonNull(name);
				this.name = name;
				Objects.requireNonNull(type);
				this.type = type;
				this.value = value;
			}

			Ann(String name) {
				Objects.requireNonNull(name);
				this.name = name;
				this.type = null;
				this.value = null;
			}

			final String name;
			final Type type;
			final Object value;

			@Override
			public String toString() {
				return name + "(" + type + ")=" + value;
			}
		}

		Map<String, Ann> fin = new HashMap<String, Ann>();

		@Override
		public AnnotationVisitor visitArray(String name) {
			if (name == null || name.isEmpty())
				throw new Error();

			AV1 outer = this;

			return new AV1() {
				@Override
				public void visitEnd() {
					if (anon == null)
						throw new Error();
					if (fin.size() != 0)
						throw new Error();

					System.err.println(
							"XXX END2 " + this + " " + outer + " -- " + name + " " + anonType + " " + anon + " " + fin);
					if (anonType == null && anon.size() == 0)
						outer.add(new Ann(name));
					else
						outer.add(new Ann(name, TypeUtil.toArray(anonType), anon));
				}
			};
		}

		protected void add(Ann ann) {
			Object old = fin.putIfAbsent(ann.name, ann);
			if (old != null)
				throw new Error();
		}

		@Override
		public void visitEnd() {
			super.visitEnd();
		}
	}

	static final Type javaLangObject = Type.getObjectType("java/lang/Object");

	static final Type javaLangBoolean = Type.getObjectType("java/lang/Boolean");
	static final Type javaLangEnum = Type.getObjectType("java/lang/Enum");
	static final Type javaLangDouble = Type.getObjectType("java/lang/Double");
	static final Type javaLangInteger = Type.getObjectType("java/lang/Integer");
	static final Type javaLangLong = Type.getObjectType("java/lang/Long");
	static final Type javaLangString = Type.getObjectType("java/lang/String");

	static final Type javaMathBigDecimal = Type.getObjectType("java/math/BigDecimal");

	static final Type javaUtilDate = Type.getObjectType("java/util/Date");

	static final Type javaSqlTime = Type.getObjectType("java/sql/Time");

	static final Type orgJodaTimeDateTime = Type.getObjectType("org/joda/time/DateTime");
	static final Type orgJodaTimeLocalDate = Type.getObjectType("org/joda/time/LocalDate");
	static final Type orgJodaTimeLocalTime = Type.getObjectType("org/joda/time/LocalTime");

	static final Type embeddable = Type.getObjectType("javax/persistence/Embeddable");
	static final Type entity = Type.getObjectType("javax/persistence/Entity");
	static final Type javaxPersistenceTable = Type.getObjectType("javax/persistence/Table");
	static final Type javaxPersistenceAccess = Type.getObjectType("javax/persistence/Access");
	static final Type cacheable = Type.getObjectType("javax/persistence/Cacheable");
	static final Type mappedSuperclass = Type.getObjectType("javax/persistence/MappedSuperclass");
	static final Type idClass = Type.getObjectType("javax/persistence/IdClass");
	static final Type entityListeners = Type.getObjectType("javax/persistence/EntityListeners");

	static final Type javaxPersistenceColumn = Type.getObjectType("javax/persistence/Column");
	static final Type javaxPersistenceEmbedded = Type.getObjectType("javax/persistence/Embedded");
	static final Type javaxPersistenceTransient = Type.getObjectType("javax/persistence/Transient");
	static final Type javaxPersistenceAttributeOverride = Type.getObjectType("javax/persistence/AttributeOverride");
	static final Type javaxPersistenceAttributeOverrides = Type.getObjectType("javax/persistence/AttributeOverrides");
	static final Type javaxPersistenceEnumerated = Type.getObjectType("javax/persistence/Enumerated");
	static final Type javaxPersistenceManyToOne = Type.getObjectType("javax/persistence/ManyToOne");
	static final Type javaxPersistenceJoinColumn = Type.getObjectType("javax/persistence/JoinColumn");
	static final Type javaxPersistenceOneToOne = Type.getObjectType("javax/persistence/OneToOne");
	static final Type javaxPersistenceOneToMany = Type.getObjectType("javax/persistence/OneToMany");
	static final Type javaxPersistenceBasic = Type.getObjectType("javax/persistence/Basic");
	static final Type javaxPersistenceOrderColumn = Type.getObjectType("javax/persistence/OrderColumn");
	static final Type javaxPersistenceId = Type.getObjectType("javax/persistence/Id");
	static final Type javaxPersistenceSequenceGenerator = Type.getObjectType("javax/persistence/SequenceGenerator");
	static final Type javaxPersistenceGeneratedValue = Type.getObjectType("javax/persistence/GeneratedValue");
	static final Type javaxPersistenceVersion = Type.getObjectType("javax/persistence/Version");
	static final Type javaxPersistenceManyToMany = Type.getObjectType("javax/persistence/ManyToMany");
	static final Type javaxPersistenceJoinTable = Type.getObjectType("javax/persistence/JoinTable");
	static final Type javaxPersistenceEmbeddedId = Type.getObjectType("javax/persistence/EmbeddedId");
	static final Type javaxPersistenceMapsId = Type.getObjectType("javax/persistence/MapsId");
	static final Type javaxPersistenceJoinColumns = Type.getObjectType("javax/persistence/JoinColumns");
	static final Type javaxPersistencePrimaryKeyJoinColumns = Type
			.getObjectType("javax/persistence/PrimaryKeyJoinColumns");
	static final Type javaxPersistenceTemporal = Type.getObjectType("javax/persistence/Temporal");
	static final Type javaxPersistenceOrderBy = Type.getObjectType("javax/persistence/OrderBy");
	static final Type javaxPersistenceLob = Type.getObjectType("javax/persistence/Lob");
	static final Type javaxPersistenceElementCollection = Type.getObjectType("javax/persistence/ElementCollection");
	static final Type javaxPersistenceCollectionTable = Type.getObjectType("javax/persistence/CollectionTable");
	static final Type javaxPersistencePrePersist = Type.getObjectType("javax/persistence/PrePersist");
	static final Type javaxPersistencePreUpdate = Type.getObjectType("javax/persistence/PreUpdate");
	static final Type javaxPersistencePostPersist = Type.getObjectType("javax/persistence/PostPersist");
	static final Type javaxPersistencePostUpdate = Type.getObjectType("javax/persistence/PostUpdate");
	static final Type javaxPersistencePersistenceContext = Type.getObjectType("javax/persistence/PersistenceContext");

	static enum FieldType {
		ONE_TO_MANY, MANY_TO_ONE, ONE_TO_ONE, MANY_TO_MANY, EMBEDDED, VALUE, ELEMENT_COLLECTION
	};

	static class TResult {
		private Map<String, JpaProperty> fieldProperties;
		private Map<String, JpaProperty> methodProperties;
	}

	static interface Model<T> {
		T create(Map<String, Ann> fin);
	}

	static class FieldsModel<T> implements Model<T> {
		static class Element {
			Function<Ann, Object> parser;
			Field field;
		}

		Class<T> clazz;
		Map<String, Element> fields = new HashMap<>();

		public T create(Map<String, Ann> fin) {
			try {
				T result = clazz.newInstance();

				for (Map.Entry<String, Ann> e : fin.entrySet()) {
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

	static class ConstructorModel<T> implements Model<T> {
		Constructor<T> c;

		static class Element {
			Function<Ann, Object> parser;
			int idx;
		}

		Map<String, Element> fields = new HashMap<>();

		@Override
		public T create(Map<String, Ann> fin) {
			Object[] args = new Object[c.getParameterCount()];

			for (Map.Entry<String, Ann> e : fin.entrySet()) {
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

	static LoadingCache<Class<?>, Model<?>> graphs = CacheBuilder.newBuilder() //
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
						return (v) -> genericParse(c, ((Anno) v).ann);
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
							e.parser = (v) -> parser.apply(v.value);

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
							e.parser = (v) -> parser.apply(v.value);
						}

						return m;
					}
				}

			});

	private static <T> T genericParse(Class<T> clazz, Map<String, Ann> fin) {
		try {
			@SuppressWarnings("unchecked")
			Model<T> m = (Model<T>) graphs.get(clazz);

			return m.create(fin);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void analyse(ClassReader reader, ClassModel bean) {

		System.err.println("DOING JPA CLASS ANALYSIS " + bean);

		reader.accept(new ClassVisitor(Opcodes.ASM5) {
			boolean isJpa = false;

			JpaAnalysisResult result = new JpaAnalysisResult(bean);

			private Map<String, JpaProperty> fieldProperties = new LinkedHashMap<>();
			private Map<String, JpaProperty> methodProperties = new LinkedHashMap<>();

			private Map<String, String> setters = new HashMap<>();
			private Map<String, String> getters = new HashMap<>();

			JpaProperty getFieldProperty(String name) {
				JpaProperty result = fieldProperties.get(name);
				if (result == null) {
					result = new JpaProperty(name);
					fieldProperties.put(name, result);
				}
				return result;
			}

			JpaProperty getMethodProperty(String name) {
				JpaProperty result = methodProperties.get(name);
				if (result == null) {
					result = new JpaProperty(name);
					methodProperties.put(name, result);
				}
				return result;
			}

			void setType(JpaProperty prop, Type type) {
				if (prop.type == null)
					prop.setType(type);
				else if (!prop.type.equals(type))
					throw new Error("Different types for property '" + prop + "': '" + prop.type + "' '" + type + "'");
			}

			void setType2(JpaProperty prop, JavaType type) {
				if (prop.type2 == null)
					prop.setType2(type);
				else if (!prop.type.equals(type))
					throw new Error("Different types for property '" + prop + "': '" + prop.type + "' '" + type + "'");
			}

			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				System.err.println("JPA VISIT " + name);
			}

			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				if (!visible)
					return null;

				System.err.println("JPA VISIT ANN CLASS " + desc);

				Type t = Type.getType(desc);
				if (t.getClassName().startsWith("javax.persistence.")) {
					isJpa = true;
					return new AV1() {
						public void visitEnd() {
							if (!anon.isEmpty())
								throw new Error();
							System.err.println("F " + fin);

							if (t.equals(embeddable)) {
								result.setEmbeddable();
							} else if (t.equals(entity)) {
								result.setEntity();
							} else if (t.equals(javaxPersistenceTable)) {
								result.setTable(genericParse(TableAnnotation.class, fin));
							} else if (t.equals(javaxPersistenceAccess)) {
								genericParse(AccessAnnotation.class, fin);
								result.setAccess(JpaAccess.valueOf((String) fin.get("value").value));
							} else if (t.equals(cacheable)) {
							} else if (t.equals(mappedSuperclass)) {
								result.setMappedSuperclass();
							} else if (t.equals(idClass)) {
							} else if (t.equals(entityListeners)) {
							} else {
								throw new Error("" + t);
							}
						};
					};
				} else {
					return null;
				}
			}

			boolean handlePropertyAnnotation(Type t, JpaProperty property, Map<String, Ann> fin) {
				if (t.equals(javaxPersistenceColumn)) {
					property.setColumn(genericParse(ColumnAnnotation.class, fin));
				} else if (t.equals(javaxPersistenceId)) {
					property.setId(true);
				} else if (t.equals(javaxPersistenceAccess)) {
					property.setAccess(JpaAccess.valueOf((String) fin.get("value").value));
				} else if (t.equals(javaxPersistenceEmbedded)) {
					property.setFieldType(FieldType.EMBEDDED);
				} else if (t.equals(javaxPersistenceAttributeOverride)) {
					property.setAttributeOverrides(
							Collections.singleton(genericParse(AttributeOverrideAnnotation.class, fin)).stream()
									.collect(Collectors.toMap((p) -> p.name, (p) -> p.column)));
				} else if (t.equals(javaxPersistenceAttributeOverrides)) {
					property.setAttributeOverrides(genericParse(AttributeOverridesAnnotation.class, fin).value.stream()
							.collect(Collectors.toMap((p) -> p.name, (p) -> p.column)));
				} else if (t.equals(javaxPersistenceElementCollection)) {
					property.setFieldType(FieldType.ELEMENT_COLLECTION);
				} else if (t.equals(javaxPersistenceTransient)) {
					property.setTransient(true);
				} else if (t.equals(javaxPersistenceEnumerated)) {
				} else if (t.equals(javaxPersistenceManyToOne)) {
					property.setFieldType(FieldType.MANY_TO_ONE);
					property.setManyToOne(genericParse(ManyToOneAnnotation.class, fin));
				} else if (t.equals(javaxPersistenceJoinColumn)) {
					property.setJoinColumns(Collections.singletonList(genericParse(JoinColumnAnnotation.class, fin)));
				} else if (t.equals(javaxPersistenceJoinColumns)) {
					property.setJoinColumns(genericParse(JoinColumnsAnnotation.class, fin).value);
				} else if (t.equals(javaxPersistenceOneToOne)) {
					property.setFieldType(FieldType.ONE_TO_ONE);
					property.setOneToOne(genericParse(OneToOneAnnotation.class, fin));
				} else if (t.equals(javaxPersistenceBasic)) {
				} else if (t.equals(javaxPersistenceOneToMany)) {
					property.setFieldType(FieldType.ONE_TO_MANY);
					property.setOneToMany(genericParse(OneToManyAnnotation.class, fin));
				} else if (t.equals(javaxPersistenceOrderColumn)) {
					property.setOrderColumn(genericParse(OrderColumnAnnotation.class, fin));
				} else if (t.equals(javaxPersistenceSequenceGenerator)) {
				} else if (t.equals(javaxPersistenceGeneratedValue)) {
				} else if (t.equals(javaxPersistenceVersion)) {
				} else if (t.equals(javaxPersistenceManyToMany)) {
					property.setFieldType(FieldType.MANY_TO_MANY);
					property.setManyToMany(genericParse(ManyToManyAnnotation.class, fin));
				} else if (t.equals(javaxPersistenceJoinTable)) {
					property.setJoinTable(genericParse(JoinTableAnnotation.class, fin));
				} else if (t.equals(javaxPersistenceEmbeddedId)) {
					property.setFieldType(FieldType.EMBEDDED);
					property.setId(true);
				} else if (t.equals(javaxPersistenceMapsId)) {
				} else if (t.equals(javaxPersistencePrimaryKeyJoinColumns)) {
				} else if (t.equals(javaxPersistenceTemporal)) {
				} else if (t.equals(javaxPersistenceAccess)) {
					property.setAccess(JpaAccess.valueOf((String) fin.get("value").value));
				} else if (t.equals(javaxPersistenceOrderBy)) {
				} else if (t.equals(javaxPersistenceLob)) {
				} else if (t.equals(javaxPersistenceElementCollection)) {
					property.setFieldType(FieldType.ELEMENT_COLLECTION);
				} else if (t.equals(javaxPersistenceCollectionTable)) {
					property.setCollectionTable(genericParse(CollectionTableAnnotation.class, fin));
				} else {
					return false;
				}
				return true;
			}

			@Override
			public void visitEnd() {
				System.err.println("END " + bean + " " + isJpa + " " + result);

				if (!isJpa && false)
					return;

				TResult r = new TResult();
				assert fieldProperties != null;
				r.fieldProperties = fieldProperties;
				assert methodProperties != null;
				r.methodProperties = methodProperties;
				// if (result.getAccess() == null) {
				// switch (result.getKind()) {
				// case ENTITY:
				// if (methodProperties.values().stream().filter(p ->
				// p.id).findAny().isPresent()) {
				// result.setAccess(JpaAccess.PROPERTY);
				// } else if (fieldProperties.values().stream().filter(p ->
				// p.id).findAny().isPresent()) {
				// result.setAccess(JpaAccess.FIELD);
				// } else {
				// throw new Error();
				// }
				// break;
				// case EMBEDDABLE:
				// result.setAccess(JpaAccess.PROPERTY);
				// break;
				// }
				// }
				//
				// switch (result.getAccess()) {
				// case FIELD:
				// result.properties =
				// fieldProperties.entrySet().stream().filter(p ->
				// !p.getValue().trans)
				// .collect(Collectors.toMap(e -> e.getKey(), e ->
				// e.getValue()));
				// break;
				// case PROPERTY:
				// result.properties =
				// methodProperties.entrySet().stream().filter(p ->
				// !p.getValue().trans)
				// .collect(Collectors.toMap(e -> e.getKey(), e ->
				// e.getValue()));
				// break;
				// }

				bean.add(JpaAnalysisResult.class, result);
				bean.add(TResult.class, r);
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature,
					String[] exceptions) {

				// omit methods builds for generics
				if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
					return null;
				}

				System.err.println("START METHOD " + name + " " + desc + " " + signature);

				MethodSignature methodSignature = AsmTypeParser.parseMethodSignature(desc, signature, exceptions);
				if (!isJpa && false)
					return null;

				Type methodType = Type.getMethodType(desc);

				JpaProperty property;

				if (name.startsWith("get") && name.length() > 3 && methodType.getArgumentTypes().length == 0
						&& !methodType.getReturnType().equals(Type.VOID_TYPE)) {
					getters.put(PropertyNaming.toProperty(name), name);
					property = getMethodProperty(PropertyNaming.toProperty(name));
					setType(property, methodType.getReturnType());
					setType2(property, methodSignature.getReturnType());
				} else if (name.startsWith("set") && name.length() > 3 && methodType.getArgumentTypes().length == 1
						&& methodType.getReturnType().equals(Type.VOID_TYPE)) {
					setters.put(PropertyNaming.toProperty(name), name);
					return null;
				} else {
					return null;
				}

				return new MethodVisitor(Opcodes.ASM5) {
					boolean col = false;

					@Override
					public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
						System.err.println("JPA VISIT ANN METHOD " + desc);

						Type t = Type.getType(desc);
						if (t.getClassName().startsWith("javax.persistence.")) {
							return new AV1() {
								public void visitEnd() {
									if (!anon.isEmpty())
										throw new Error();
									System.err.println("F " + t + " " + fin);
									if (handlePropertyAnnotation(t, property, fin)) {
									} else if (t.equals(javaxPersistencePrePersist)) {
									} else if (t.equals(javaxPersistencePreUpdate)) {
									} else if (t.equals(javaxPersistencePostPersist)) {
									} else if (t.equals(javaxPersistencePostUpdate)) {
									} else {
										throw new Error("" + t);
									}
								};
							};
						} else {
							return null;
						}

					}

					@Override
					public void visitEnd() {
						super.visitEnd();
					}
				};
			}

			@Override
			public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
				if ((access & Opcodes.ACC_STATIC) != 0)
					return null;

				System.err.println("START FIELD " + name + " " + desc + " " + signature + " " + value);

				JavaType type2 = AsmTypeParser.parseFieldSignature(desc, signature);

				if (!isJpa && false)
					return null;

				Type tt = Type.getType(desc);

				JpaProperty property = getFieldProperty(name);
				property.setType(tt);
				property.setType2(type2);

				return new FieldVisitor(Opcodes.ASM5) {
					@Override
					public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
						System.err.println("JPA VISIT ANN FIELD " + desc);

						Type t = Type.getType(desc);
						if (t.getClassName().startsWith("javax.persistence.")) {
							return new AV1() {
								public void visitEnd() {
									if (!anon.isEmpty())
										throw new Error();
									System.err.println("F " + fin);

									if (handlePropertyAnnotation(t, property, fin)) {
									} else if (t.equals(javaxPersistencePersistenceContext)) {
									} else {
										throw new Error("" + t);
									}
								};
							};
						} else {
							return null;
						}

					}

					@Override
					public void visitEnd() {
					}
				};
			}

		}, 0);
	}

	static boolean isBasicType(Type t) {
		return t.equals(Type.BOOLEAN_TYPE) || t.equals(Type.BYTE_TYPE) || t.equals(Type.CHAR_TYPE)
				|| t.equals(Type.DOUBLE_TYPE) || t.equals(Type.FLOAT_TYPE) || t.equals(Type.INT_TYPE)
				|| t.equals(Type.LONG_TYPE) || t.equals(Type.SHORT_TYPE);
	}

	boolean findId(ClassHierarchy ch, ClassModel t, Function<TResult, Map<String, ? extends JpaProperty>> e) {
		Objects.requireNonNull(t);

		TResult r = t.get(TResult.class);
		if (r != null)
			if (e.apply(r).values().stream().findFirst().filter(p -> p.id).isPresent())
				return true;

		ClassModel t2 = ch.get(t.getSuperType().getRawType());
		if (t2 == null)
			return false;
		return findId(ch, t2, e);
	}

	void collectProperties(ClassHierarchy ch, ClassModel t, JpaAccess a, Consumer<JpaProperty> dest,
			Map<String, ColumnAnnotation> overrides, Predicate<JpaAnalysisResult> filter) {
		Objects.requireNonNull(t);

		ClassModel t2 = ch.get(t.getSuperType().getRawType());
		if (t2 != null) {
			collectProperties(ch, t2, a, dest, overrides, p -> p.isMappedSuperclass());
		}

		TResult r = t.get(TResult.class);
		if (r == null)
			return;

		JpaAnalysisResult result = t.get(JpaAnalysisResult.class);
		if (result == null)
			throw new Error();

		if (!filter.test(result))
			return;

		Map<String, ? extends JpaProperty> props;

		switch (a) {
		case FIELD:
			props = r.fieldProperties;
			break;
		case PROPERTY:
			props = r.methodProperties;
			break;
		default:
			throw new Error();
		}

		for (Map.Entry<String, ? extends JpaProperty> e : props.entrySet()) {
			if (e.getValue().trans)
				continue;
			if (e.getValue().fieldType == null) {
				detectType(ch, e.getValue());
			}
			switch (e.getValue().fieldType) {
			case VALUE:
				JpaProperty p1 = e.getValue();
				ColumnAnnotation override = overrides.get(e.getKey());
				if (override != null)
					p1 = p1.withColumn(override);
				dest.accept(p1);
				break;
			case EMBEDDED:
				t2 = ch.get(e.getValue().type);
				Map<String, ColumnAnnotation> nextOverrides;
				nextOverrides = createOverride(overrides, e.getKey(), e.getValue().attributeOverrides);
				collectProperties(ch, t2, a, dest, nextOverrides, (p) -> true);
				break;
			default:
				System.err.println("UNHANDLED");
				dest.accept(e.getValue());
				break;
			}
		}
	}

	private Map<String, ColumnAnnotation> createOverride(Map<String, ColumnAnnotation> overrides, String key,
			Map<String, ColumnAnnotation> attributeOverrides) {
		Map<String, ColumnAnnotation> result = new HashMap<>();

		String prefix = key + ".";
		for (Entry<String, ColumnAnnotation> e : overrides.entrySet())
			if (e.getKey().startsWith(prefix))
				result.put(e.getKey().substring(prefix.length()), e.getValue());

		if (attributeOverrides != null)
			for (Entry<String, ColumnAnnotation> e : attributeOverrides.entrySet())
				result.putIfAbsent(e.getKey(), e.getValue());

		return result;
	}

	void detectType(ClassHierarchy ch, JpaProperty e) {
		if (e.type == null)
			throw new Error("no type " + e);

		if (isBasicType(e.type)) {
			e.setFieldType(FieldType.VALUE);
		} else if (TypeUtil.isArrayOf(e.type, Type.BYTE_TYPE)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangBoolean)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangDouble)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangInteger)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangLong)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangString)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaUtilDate)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaSqlTime)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaMathBigDecimal)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(orgJodaTimeDateTime)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(orgJodaTimeLocalDate)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(orgJodaTimeLocalTime)) {
			e.setFieldType(FieldType.VALUE);
		} else {
			ClassModel classModel = ch.get(e.type);
			if (classModel == null)
				throw new Error("no class " + e + " " + e.type);
			if (classModel.getSuperType().getRawType().equals(javaLangEnum)) {
				e.setFieldType(FieldType.VALUE);
			} else {
				JpaAnalysisResult result2 = classModel.get(JpaAnalysisResult.class);
				if (result2 != null && result2.isEmbeddable()) {
					e.setFieldType(FieldType.EMBEDDED);
				} else {
					throw new Error("no type " + e.name + " " + e.type + " " + result2 + " " + (result2 != null
							? result2.isEntity() + " " + result2.isEmbeddable() + " " + result2.isMappedSuperclass()
							: "<n.a.>"));
				}
			}
		}
	}

	@Override
	public void phase2(ClassModel bean, ClassHierarchy ch) {

		JpaAnalysisResult result = bean.get(JpaAnalysisResult.class);
		if (result == null)
			return;

		if (result.isEntity()) {
			JpaAccess defaultAccess;

			if (findId(ch, bean, t -> t.methodProperties)) {
				defaultAccess = JpaAccess.PROPERTY;
			} else {
				defaultAccess = JpaAccess.FIELD;
			}

			Map<String, JpaProperty> properties = new HashMap<>();

			collectProperties(ch, bean, defaultAccess, (v) -> properties.put(v.name, v), Collections.emptyMap(),
					(p) -> {
						if (!p.isEntity())
							throw new Error();
						return true;
					});

			for (JpaProperty p : properties.values()) {
				switch (p.fieldType) {
				case MANY_TO_MANY:
				case ONE_TO_MANY:
					Type elementType = p.type2.accept(new JavaTypeVisitor<Type>() {
						public Type visit(ParameterizedType type) {
							if (type.getTypeArguments().size() != 1)
								throw new Error();
							TypeArgument argument = type.getTypeArguments().get(0);
							return argument.getType().accept(new JavaTypeVisitor<Type>() {
								public Type visit(RawType rawType) {
									return rawType.getRawType();
								};

								public Type visit(ParameterizedType parametrizedType) {
									return parametrizedType.getRawType();
								};
							});
						}
					});
					p.elementType = elementType;
					break;
				case ELEMENT_COLLECTION:
					elementType = p.type2.accept(new JavaTypeVisitor<Type>() {
						public Type visit(ParameterizedType type) {
							if (type.getTypeArguments().size() != 1)
								throw new Error();
							TypeArgument argument = type.getTypeArguments().get(0);
							return argument.getType().accept(new JavaTypeVisitor<Type>() {
								public Type visit(RawType rawType) {
									return rawType.getRawType();
								};

								public Type visit(ParameterizedType parametrizedType) {
									return parametrizedType.getRawType();
								};
							});
						}
					});

					Map<String, JpaProperty> properties2 = new HashMap<>();

					Map<String, ColumnAnnotation> nextOverrides;
					if (p.attributeOverrides != null)
						nextOverrides = p.attributeOverrides;
					else
						nextOverrides = Collections.emptyMap();

					collectProperties(ch, ch.get(elementType), defaultAccess, (v) -> properties2.put(v.name, v),
							nextOverrides, (p1) -> true);

					p.collectionTableProperties = properties2.values();

					break;
				default:
					break;
				}
			}
			result.properties = properties;
		}
	}

}
