package com.github.da.jpa;

import static com.github.da.JodaTypes.orgJodaTimeDateTime;
import static com.github.da.JodaTypes.orgJodaTimeLocalDate;
import static com.github.da.JodaTypes.orgJodaTimeLocalTime;
import static com.github.da.JreTypes.javaLangBoolean;
import static com.github.da.JreTypes.javaLangByte;
import static com.github.da.JreTypes.javaLangCharacter;
import static com.github.da.JreTypes.javaLangDouble;
import static com.github.da.JreTypes.javaLangEnum;
import static com.github.da.JreTypes.javaLangFloat;
import static com.github.da.JreTypes.javaLangInteger;
import static com.github.da.JreTypes.javaLangLong;
import static com.github.da.JreTypes.javaLangString;
import static com.github.da.JreTypes.javaMathBigDecimal;
import static com.github.da.JreTypes.javaSqlDate;
import static com.github.da.JreTypes.javaSqlTime;
import static com.github.da.JreTypes.javaUtilDate;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.objectweb.asm.Type;

import com.github.da.AnalysisResult;
import com.github.da.Archive;
import com.github.da.ClassData;
import com.github.da.ClassLoader;
import com.github.da.DeploymentArtifacts;
import com.github.da.JpaAccess;
import com.github.da.TypeUtil;
import com.github.da.t.RootAnalysis;

import asm.ClassSignature;
import asm.JavaTypeVisitor;
import asm.ParameterizedType;
import asm.RawType;
import asm.TypeArgument;

public class JpaModelCreatorPhase2 implements RootAnalysis {

	@Inject
	AnalysisResult ar;

	@Override
	public void run() {

		for (Archive da : ar.get(DeploymentArtifacts.class)) {
			ch = da.getClassLoader();
			List<PersistenceUnit> resultUnits = new LinkedList<>();

			Collection<PersistenceXmlUnits> all = ch.getAll(Paths.get("META-INF/persistence.xml"),
					PersistenceXmlUnits.class);
			for (PersistenceXmlUnits units : all) {
				for (PersistenceXmlUnit unit : units.getUnits()) {
					Map<ClassData, JpaAnalysisResult2> result = new HashMap<>();

					for (String s : unit.classes) {

						ClassData cd = ch.get(Type.getObjectType(s.replace('.', '/')));

						phase2(cd);

						result.put(cd, cd.get(JpaAnalysisResult2.class));
					}
					System.err.println("XXX " + unit.excludeUnlistedClasses + " " + ch);
					if (!unit.excludeUnlistedClasses) {
						// FIXME: restrict to archive
						for (ClassData cd : ch.getClasses()) {
							phase2(cd);
							result.put(cd, cd.get(JpaAnalysisResult2.class));
						}

					}

					resultUnits.add(new PersistenceUnit(unit, result));
				}
			}

			da.put(PersistenceUnits.class, new PersistenceUnits(resultUnits));
		}
	}

	ClassLoader ch;

	boolean findId(ClassData t, Function<TResult, Map<String, ? extends JpaProperty>> e) {
		Objects.requireNonNull(t);

		TResult r = t.get(TResult.class);
		if (r != null)
			if (e.apply(r).values().stream().findFirst().filter(p -> p.id).isPresent())
				return true;

		ClassData t2 = ch.find(t.get(ClassSignature.class).getSuperclass().getRawType());
		if (t2 == null)
			return false;
		return findId(t2, e);
	}

	public void phase2(ClassData bean) {
		JpaAnalysisResult2 result = bean.find(JpaAnalysisResult2.class);
		if (result == null)
			return;

		if (result.isEntity()) {
			JpaAccess defaultAccess;

			if (findId(bean, t -> t.methodProperties)) {
				defaultAccess = JpaAccess.PROPERTY;
			} else {
				defaultAccess = JpaAccess.FIELD;
			}

			Map<String, JpaProperty> properties = new HashMap<>();

			collectProperties(bean, defaultAccess, (v) -> {
				JpaProperty old = properties.putIfAbsent(v.name, v);
				if (old != null)
					throw new IllegalArgumentException();
			} , Collections.emptyMap(), (p) -> {
				if (!p.isEntity())
					throw new Error();
				return true;
			} , "");

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

					collectProperties(ch.get(elementType), defaultAccess, (v) -> {
						JpaProperty old = properties2.putIfAbsent(v.name, v);
						if (old != null)
							throw new IllegalArgumentException();
					} , nextOverrides, (p1) -> true, "" /* FIXME */);

					p.collectionTableProperties = properties2.values();

					break;
				default:
					break;
				}
			}
			result.properties = properties;
		}
	}

	void collectProperties(ClassData t, JpaAccess defaultJpaAccess, Consumer<JpaProperty> dest,
			Map<String, ColumnAnnotation> overrides, Predicate<JpaAnalysisResult2> filter, String prefix) {
		Objects.requireNonNull(t);

		ClassData t2 = ch.find(t.get(ClassSignature.class).getSuperclass().getRawType());
		if (t2 != null) {
			collectProperties(t2, defaultJpaAccess, dest, overrides, p -> p.isMappedSuperclass(), prefix);
		}

		TResult r = t.get(TResult.class);
		if (r == null)
			return;

		JpaAnalysisResult2 result = t.get(JpaAnalysisResult2.class);
		if (result == null)
			throw new Error();

		if (!filter.test(result))
			return;

		Map<String, JpaProperty> props;

		switch (result.getAccess() != null ? result.getAccess() : defaultJpaAccess) {
		case FIELD:
			props = r.fieldProperties;
			r.methodProperties.values().stream().filter(p -> p.access == JpaAccess.PROPERTY)
					.forEach((p) -> props.put(p.name, p));
			break;
		case PROPERTY:
			props = r.methodProperties;
			r.fieldProperties.values().stream().filter(p -> p.access == JpaAccess.FIELD)
					.forEach((p) -> props.put(p.name, p));
			break;
		default:
			throw new Error();
		}

		for (Map.Entry<String, ? extends JpaProperty> e : props.entrySet()) {
			if (e.getValue().trans)
				continue;
			if (e.getValue().fieldType == null) {
				detectType(e.getValue());
			}
			switch (e.getValue().fieldType) {
			case VALUE:
				JpaProperty p1 = e.getValue();
				ColumnAnnotation override = overrides.get(e.getKey());
				if (override != null)
					p1 = p1.withColumn(override);
				dest.accept(addPrefix(p1, prefix));
				break;
			case EMBEDDED:
				t2 = ch.get(e.getValue().type);
				Map<String, ColumnAnnotation> nextOverrides;
				nextOverrides = createOverride(overrides, e.getKey(), e.getValue().attributeOverrides);
				collectProperties(t2, defaultJpaAccess, dest, nextOverrides, (p) -> true,
						joinPrefix(prefix, e.getValue().name));
				break;
			default:
				System.err.println("UNHANDLED");
				dest.accept(addPrefix(e.getValue(), prefix));
				break;
			}
		}
	}

	static JpaProperty addPrefix(JpaProperty p, String prefix) {
		if (prefix.isEmpty())
			return p;
		else
			return p.withName(prefix + "." + p.name);
	}

	static String joinPrefix(String prefix1, String prefix2) {
		return prefix1.isEmpty() ? prefix2 : prefix1 + "." + prefix2;
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

	static boolean isBasicType(Type t) {
		return t.equals(Type.BOOLEAN_TYPE) || t.equals(Type.BYTE_TYPE) || t.equals(Type.CHAR_TYPE)
				|| t.equals(Type.DOUBLE_TYPE) || t.equals(Type.FLOAT_TYPE) || t.equals(Type.INT_TYPE)
				|| t.equals(Type.LONG_TYPE) || t.equals(Type.SHORT_TYPE);
	}

	void detectType(JpaProperty e) {
		if (e.type == null)
			throw new Error("no type " + e);

		if (isBasicType(e.type)) {
			e.setFieldType(FieldType.VALUE);
		} else if (TypeUtil.isArrayOf(e.type, Type.BYTE_TYPE)) {
			e.setFieldType(FieldType.VALUE);
		} else if (TypeUtil.isArrayOf(e.type, Type.CHAR_TYPE)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangBoolean)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangByte)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangCharacter)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangDouble)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangFloat)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangInteger)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangLong)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaLangString)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaUtilDate)) {
			e.setFieldType(FieldType.VALUE);
		} else if (e.type.equals(javaSqlDate)) {
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
			ClassData classData = ch.get(e.type);
			if (classData == null)
				throw new Error("no class " + e + " " + e.type);
			if (classData.get(ClassSignature.class).getSuperclass().getRawType().equals(javaLangEnum)) {
				e.setFieldType(FieldType.VALUE);
			} else {
				JpaAnalysisResult2 result2 = classData.get(JpaAnalysisResult2.class);
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

}
