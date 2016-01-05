package com.github.da.jpa;

import static com.github.da.jpa.JpaTypes.javaxPersistencePersistenceContext;
import static com.github.da.jpa.JpaTypes.javaxPersistencePostPersist;
import static com.github.da.jpa.JpaTypes.javaxPersistencePostUpdate;
import static com.github.da.jpa.JpaTypes.javaxPersistencePrePersist;
import static com.github.da.jpa.JpaTypes.javaxPersistencePreUpdate;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.da.AnalysisResult;
import com.github.da.AsmIds;
import com.github.da.ClassData;
import com.github.da.ClassHierarchy;
import com.github.da.Include;
import com.github.da.PropertyNaming;
import com.github.pms1.asm.annotation.AnnotationData;
import com.github.pms1.asm.annotation.converter.AnnotationConverter;
import com.github.pms1.c4.classes.annotations.AnnotationModel;

import asm.JavaType;
import asm.MethodSignature;
import ts.AsmTypeParser;

@Include(JpaModelCreatorPhase2.class)
public class JpaModelCreator implements com.github.da.t.ClassProcessor {

	@Inject
	AnalysisResult ar;

	@Inject
	JpaModelCreatorConfig config;

	@Override
	public void run(ClassReader v) {
		ClassHierarchy ch = ar.get(ClassHierarchy.class);

		v.accept(new ClassVisitor(Opcodes.ASM5) {

			ClassData cd;

			JpaAnalysisResult2 result;

			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				cd = ch.get(AsmIds.forClass(name));
				result = new JpaAnalysisResult2(cd);

				AnnotationModel am = cd.get(AnnotationModel.class);

				for (Map.Entry<Type, AnnotationData> e : am) {
					Type t = e.getKey();

					if (t.equals(JpaTypes.embeddable)) {
						result.setEmbeddable();
					} else if (t.equals(JpaTypes.entity)) {
						result.setEntity();
					} else if (t.equals(JpaTypes.javaxPersistenceTable)) {
						result.setTable(AnnotationConverter.convertAnnotation(TableAnnotation.class, e.getValue()));
					} else if (t.equals(JpaTypes.javaxPersistenceAccess)) {
						result.setAccess(
								AnnotationConverter.convertAnnotation(AccessAnnotation.class, e.getValue()).value);
					} else if (t.equals(JpaTypes.cacheable)) {
					} else if (t.equals(JpaTypes.mappedSuperclass)) {
						result.setMappedSuperclass();
					} else if (t.equals(JpaTypes.idClass)) {
					} else if (t.equals(JpaTypes.entityListeners)) {
					} else {
						throw new Error("" + t);
					}

				}

				// throw new Error();
			}

			boolean handlePropertyAnnotation(Type t, JpaProperty property, AnnotationData fin) {
				if (t.equals(JpaTypes.javaxPersistenceColumn)) {
					property.setColumn(AnnotationConverter.convertAnnotation(ColumnAnnotation.class, fin));
				} else if (t.equals(JpaTypes.javaxPersistenceId)) {
					property.setId(true);
				} else if (t.equals(JpaTypes.javaxPersistenceAccess)) {
					property.setAccess(AnnotationConverter.convertAnnotation(AccessAnnotation.class, fin).value);
				} else if (t.equals(JpaTypes.javaxPersistenceEmbedded)) {
					property.setFieldType(FieldType.EMBEDDED);
				} else if (t.equals(JpaTypes.javaxPersistenceAttributeOverride)) {
					property.setAttributeOverrides(Collections
							.singleton(AnnotationConverter.convertAnnotation(AttributeOverrideAnnotation.class, fin))
							.stream().collect(Collectors.toMap((p) -> p.name, (p) -> p.column)));
				} else if (t.equals(JpaTypes.javaxPersistenceAttributeOverrides)) {
					property.setAttributeOverrides(
							AnnotationConverter.convertAnnotation(AttributeOverridesAnnotation.class, fin).value
									.stream().collect(Collectors.toMap((p) -> p.name, (p) -> p.column)));
				} else if (t.equals(JpaTypes.javaxPersistenceElementCollection)) {
					property.setFieldType(FieldType.ELEMENT_COLLECTION);
				} else if (t.equals(JpaTypes.javaxPersistenceTransient)) {
					property.setTransient(true);
				} else if (t.equals(JpaTypes.javaxPersistenceEnumerated)) {
					property.setEnumType(AnnotationConverter.convertAnnotation(EnumeratedAnnotation.class, fin).value);
				} else if (t.equals(JpaTypes.javaxPersistenceManyToOne)) {
					property.setFieldType(FieldType.MANY_TO_ONE);
					property.setManyToOne(AnnotationConverter.convertAnnotation(ManyToOneAnnotation.class, fin));
				} else if (t.equals(JpaTypes.javaxPersistenceJoinColumn)) {
					property.setJoinColumns(Collections
							.singletonList(AnnotationConverter.convertAnnotation(JoinColumnAnnotation.class, fin)));
				} else if (t.equals(JpaTypes.javaxPersistenceJoinColumns)) {
					property.setJoinColumns(
							AnnotationConverter.convertAnnotation(JoinColumnsAnnotation.class, fin).value);
				} else if (t.equals(JpaTypes.javaxPersistenceOneToOne)) {
					property.setFieldType(FieldType.ONE_TO_ONE);
					property.setOneToOne(AnnotationConverter.convertAnnotation(OneToOneAnnotation.class, fin));
				} else if (t.equals(JpaTypes.javaxPersistenceBasic)) {
				} else if (t.equals(JpaTypes.javaxPersistenceOneToMany)) {
					property.setFieldType(FieldType.ONE_TO_MANY);
					property.setOneToMany(AnnotationConverter.convertAnnotation(OneToManyAnnotation.class, fin));
				} else if (t.equals(JpaTypes.javaxPersistenceOrderColumn)) {
					property.setOrderColumn(AnnotationConverter.convertAnnotation(OrderColumnAnnotation.class, fin));
				} else if (t.equals(JpaTypes.javaxPersistenceSequenceGenerator)) {
				} else if (t.equals(JpaTypes.javaxPersistenceGeneratedValue)) {
				} else if (t.equals(JpaTypes.javaxPersistenceVersion)) {
				} else if (t.equals(JpaTypes.javaxPersistenceManyToMany)) {
					property.setFieldType(FieldType.MANY_TO_MANY);
					property.setManyToMany(AnnotationConverter.convertAnnotation(ManyToManyAnnotation.class, fin));
				} else if (t.equals(JpaTypes.javaxPersistenceJoinTable)) {
					property.setJoinTable(AnnotationConverter.convertAnnotation(JoinTableAnnotation.class, fin));
				} else if (t.equals(JpaTypes.javaxPersistenceEmbeddedId)) {
					property.setFieldType(FieldType.EMBEDDED);
					property.setId(true);
				} else if (t.equals(JpaTypes.javaxPersistenceMapsId)) {
				} else if (t.equals(JpaTypes.javaxPersistencePrimaryKeyJoinColumns)) {
				} else if (t.equals(JpaTypes.javaxPersistenceTemporal)) {
					property.setTemporalType(
							AnnotationConverter.convertAnnotation(TemporalAnnotation.class, fin).value);
				} else if (t.equals(JpaTypes.javaxPersistenceOrderBy)) {
				} else if (t.equals(JpaTypes.javaxPersistenceLob)) {
					AnnotationConverter.convertAnnotation(LobAnnotation.class, fin);
					property.setLob(true);
				} else if (t.equals(JpaTypes.javaxPersistenceElementCollection)) {
					property.setFieldType(FieldType.ELEMENT_COLLECTION);
				} else if (t.equals(JpaTypes.javaxPersistenceCollectionTable)) {
					property.setCollectionTable(
							AnnotationConverter.convertAnnotation(CollectionTableAnnotation.class, fin));
				} else if (t.equals(JpaTypes.javaxPersistenceMapKey)) {
				} else {
					return false;
				}
				return true;
			}

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
			public MethodVisitor visitMethod(int access, String name, String desc, String signature,
					String[] exceptions) {

				AnnotationModel am = cd.get(AsmIds.forMethod(name, desc)).get(AnnotationModel.class);

				// omit methods generated by the compiler for generics
				if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
					return null;
				}

				MethodSignature methodSignature = AsmTypeParser.parseMethodSignature(desc, signature, exceptions);

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

				for (Map.Entry<Type, AnnotationData> e : am) {
					Type t = e.getKey();

					if (handlePropertyAnnotation(e.getKey(), property, e.getValue())) {
					} else if (t.equals(javaxPersistencePrePersist)) {
					} else if (t.equals(javaxPersistencePreUpdate)) {
					} else if (t.equals(javaxPersistencePostPersist)) {
					} else if (t.equals(javaxPersistencePostUpdate)) {
					} else {
						throw new Error("" + t);
					}
				}

				return super.visitMethod(access, name, desc, signature, exceptions);
			}

			@Override
			public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
				if ((access & Opcodes.ACC_STATIC) != 0)
					return null;

				AnnotationModel am = cd.get(AsmIds.forField(name)).get(AnnotationModel.class);

				JavaType type2 = AsmTypeParser.parseFieldSignature(desc, signature);

				Type tt = Type.getType(desc);

				JpaProperty property = getFieldProperty(name);
				property.setType(tt);
				property.setType2(type2);

				for (Map.Entry<Type, AnnotationData> e : am) {
					Type t = e.getKey();
					if (handlePropertyAnnotation(t, property, e.getValue())) {
					} else if (t.equals(javaxPersistencePersistenceContext)) {
					} else {
						throw new Error("" + t);
					}

				}

				return super.visitField(access, name, desc, signature, value);
			}

			@Override
			public void visitEnd() {
				TResult r = new TResult();
				assert fieldProperties != null;
				r.fieldProperties = fieldProperties;
				assert methodProperties != null;
				r.methodProperties = methodProperties;

				cd.put(JpaAnalysisResult2.class, result);
				cd.put(TResult.class, r);
			}
		}, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
	}

}
