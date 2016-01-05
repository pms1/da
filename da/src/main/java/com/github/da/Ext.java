package com.github.da;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Qualifier;

import com.github.da.t.All;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import utils.TypeUtils;
import utils.TypeVisitor;

public class Ext implements Extension {

	private static class DumpTypeVisitor implements TypeVisitor<Void> {
		String prefix;
		LinkedList<String> prefixes = new LinkedList<>();

		DumpTypeVisitor() {
			prefix = "";
		}

		DumpTypeVisitor(String prefix) {
			this.prefix = prefix;
		}

		private void pushPrefix(String prefix) {
			prefixes.push(prefix);
			this.prefix += prefix;
		}

		private void popPrefix() {
			String p = prefixes.pop();
			this.prefix = prefix.substring(0, this.prefix.length() - p.length());
		}

		@Override
		public <T> Void visit(Class<T> v) {
			System.out.println(prefix + "Class " + v.getTypeName());
			return null;
		}

		@Override
		public <D extends GenericDeclaration> Void visit(TypeVariable<D> v) {
			System.out.println(prefix + "TypeVariable " + v.getTypeName());
			pushPrefix("  ");
			System.out.println(prefix + "genericDeclaration " + v.getGenericDeclaration());

			int arg = 0;
			for (Type t : v.getBounds()) {
				System.out.println(prefix + "bound[" + arg + "]");
				pushPrefix("  ");
				TypeVisitor.accept(t, this);
				popPrefix();
				++arg;
			}
			popPrefix();
			return null;
		}

		@Override
		public Void visit(ParameterizedType v) {
			System.out.println(prefix + "ParameterizedType " + v.getTypeName());
			pushPrefix("  ");

			int arg = 0;
			for (Type t : v.getActualTypeArguments()) {
				System.out.println(prefix + "actualTypeArgument[" + arg + "]");
				pushPrefix("  ");
				TypeVisitor.accept(t, this);
				popPrefix();
				++arg;
			}
			popPrefix();
			return null;
		}

		@Override
		public Void visit(WildcardType v) {
			System.out.println(prefix + "WildcardType " + v.getTypeName());
			pushPrefix("  ");

			int arg = 0;
			for (Type t : v.getLowerBounds()) {
				System.out.println(prefix + "lowerBound[" + arg + "]");
				pushPrefix("  ");
				TypeVisitor.accept(t, this);
				popPrefix();
				++arg;
			}
			arg = 0;
			for (Type t : v.getUpperBounds()) {
				System.out.println(prefix + "upperBound[" + arg + "]");
				pushPrefix("  ");
				TypeVisitor.accept(t, this);
				popPrefix();
				++arg;
			}
			popPrefix();
			return null;
		}
	}

	public class CC1 implements AlterableContext {

		@Override
		public Class<? extends Annotation> getScope() {
			return Configuration.class;
		}

		@Override
		public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
			System.err.println("GET1 " + contextual + " " + creationalContext);
			return get(contextual);
		}

		@Override
		public <T> T get(Contextual<T> contextual) {
			if (!isActive())
				throw new ContextNotActiveException();

			if (!bindings.getLast().containsKey(contextual))
				throw new Error("Unbound bean: " + contextual);
			return (T) bindings.getLast().get(contextual);
		}

		@Override
		public boolean isActive() {
			return !bindings.isEmpty();
		}

		LinkedList<Map<ConfigurationBean<?>, Object>> bindings = new LinkedList<>();

		public void activate() {
			bindings.addLast(new HashMap<>());
		}

		public void deactivate() {
			bindings.removeLast();
		}

		@Override
		public void destroy(Contextual<?> contextual) {
			// TODO Auto-generated method stub
			throw new Error();
		}

		public <C> void bind(ConfigurationBean<?> dep, Object config) {
			Objects.requireNonNull(dep);
			Preconditions.checkState(isActive());
			Object old = bindings.getLast().putIfAbsent(dep, config);
			if (old != null)
				throw new Error();
		}
	}

	public class CC2 implements AlterableContext {

		@Override
		public Class<? extends Annotation> getScope() {
			return com.github.da.Analysis.class;
		}

		@Override
		public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
			if (!isActive())
				throw new ContextNotActiveException();

			System.err.println("CC2 CREATE " + contextual);
			return contextual.create(creationalContext);
		}

		@Override
		public <T> T get(Contextual<T> contextual) {
			if (!isActive())
				throw new ContextNotActiveException();

			return null;
		}

		@Override
		public boolean isActive() {
			return true;
		}

		@Override
		public void destroy(Contextual<?> contextual) {
			// TODO Auto-generated method stub
			throw new Error();
		}
	}

	public class CC implements Context {

		@Override
		public Class<? extends Annotation> getScope() {
			return AnaScope.class;
		}

		@Override
		public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
			throw new Error();
		}

		@Override
		public <T> T get(Contextual<T> contextual) {
			if (!beans.contains(contextual) || !isActive())
				throw new Error("c=" + contextual + " " + current);

			Class<T> beanClass = (Class<T>) ((Bean<T>) contextual).getBeanClass();

			if (beanClass.equals(AnalysisResult.class))
				return (T) current;

			Object result = bindings.get(beanClass);
			if (result != null)
				return (T) result;

			result = current.get(beanClass);
			if (result != null)
				return (T) result;

			throw new Error("No value found for '" + beanClass + "'");
		}

		@Override
		public boolean isActive() {
			return current != null;
		}

		AnalysisResult current;

		public void activate(AnalysisResult aResult) {
			Objects.requireNonNull(aResult);
			current = aResult;
		}

		public void deactivate() {
			current = null;
		}

		private final Map<Class<?>, Object> bindings = new HashMap<>();

		public void bind(Class<? extends Object> key, Object value) {
			Objects.requireNonNull(key);
			Objects.requireNonNull(value);
			if (bindings.putIfAbsent(key, value) != null)
				throw new IllegalArgumentException();
		}

		public void unbind(Class<?> key) {
			Objects.requireNonNull(key);
			if (bindings.remove(key) == null)
				throw new IllegalArgumentException();
		}
	}

	private CC cc;
	private CC1 cc1;
	private CC2 cc2;

	CC getCC() {
		return cc;
	}

	CC1 getCC1() {
		return cc1;
	}

	@ApplicationScoped
	static class OurParametersFactory {
		@Inject
		Ext e;

		@Produces
		CC foo() {
			return e.getCC();
		}

		@Produces
		CC1 foo1() {
			return e.getCC1();
		}
	}

	private Set<AnnotatedType<?>> typesAnaScope = new HashSet<>();

	private static <T> TypeToken<List<T>> listOf(TypeToken<T> t) {
		return new TypeToken<List<T>>() {
		}.where(new TypeParameter<T>() {
		}, t);
	}

	private Multimap<Bean<?>, ConfigurationBean<?>> beanDeps2 = HashMultimap.create();

	/* private */ <X> void processBean(@Observes ProcessBean<X> b) {
		for (InjectionPoint ip : b.getBean().getInjectionPoints()) {
			for (ConfigurationBean<?> b1 : configurationBeans)
				if (resolves(b1, ip))
					beanDeps2.put(b.getBean(), b1);
		}
	}

	private static boolean resolves(Bean<?> b, InjectionPoint ip) {
		TypeToken<?> tip = TypeToken.of(ip.getType());
		if (!b.getTypes().stream().anyMatch(t -> tip.isAssignableFrom(t)))
			return false;

		// FIXME ignore @NonBinding here / implement correct algorithm from CDI
		for (Annotation a : ip.getQualifiers())
			if (!b.getQualifiers().stream().anyMatch(a1 -> a1.equals(a)))
				return false;

		return true;
	}

	private List<ConfigurationBean<?>> configurationBeans = new LinkedList<>();

	private static final Annotation configuredLiteratal = new AnnotationLiteral<Configured>() {
	};

	private static final Annotation defaultLiteratal = new AnnotationLiteral<Default>() {
	};

	private static final Annotation allLiteral = new AnnotationLiteral<All>() {
	};

	private <X> void vetoOurScopeTypes(@Observes ProcessAnnotatedType<X> pat) {
		if (pat.getAnnotatedType().isAnnotationPresent(com.github.da.Analysis.class)) {
			Class elementType = (Class) pat.getAnnotatedType().getBaseType();
			Type listType = listOf(TypeToken.of(elementType)).getType();

			System.err.println("TYPE " + listType);
			configurationBeans.add(new AnalyserListBean<Object>() {

				@Override
				public Class<?> getBeanClass() {
					return List.class;
				}

				@Override
				public Set<InjectionPoint> getInjectionPoints() {
					return Collections.emptySet();
				}

				@Override
				public boolean isNullable() {
					throw new UnsupportedOperationException();
				}

				@Override
				public Object create(CreationalContext<Object> creationalContext) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void destroy(Object instance, CreationalContext<Object> creationalContext) {
					throw new UnsupportedOperationException();
				}

				@Override
				public Set<Type> getTypes() {
					return Sets.newHashSet(listType, Object.class);
				}

				@Override
				public Set<Annotation> getQualifiers() {
					return Sets.newHashSet(defaultLiteratal, allLiteral);
				}

				@Override
				public Class<? extends Annotation> getScope() {
					return Configuration.class;
				}

				@Override
				public String getName() {
					return null;
				}

				@Override
				public Set<Class<? extends Annotation>> getStereotypes() {
					return Collections.emptySet();
				}

				@Override
				public boolean isAlternative() {
					return false;
				}

				@Override
				public void accept(ConfigurationBeanVisitor visitor) {
					visitor.visit(this);
				}

				@Override
				public Class<?> getTargetClass() {
					return elementType;
				}

				public String toString() {
					return "AnalyserListBean[" + listType + "]";
				};
			});
		}

		if (pat.getAnnotatedType().isAnnotationPresent(Configuration.class)) {
			pat.veto();

			if (!getQualifiers(pat.getAnnotatedType()).isEmpty())
				throw new Error();

			configurationBeans
					.add(createConfigurationConfigurationBean(TypeToken.of(pat.getAnnotatedType().getBaseType())));

			for (AnnotatedField<? super X> f : pat.getAnnotatedType().getFields()) {
				TypeToken<?> x = TypeToken.of(f.getBaseType());
				if (x.getRawType().equals(List.class)) {
					TypeToken<?> type = x.resolveType(List.class.getTypeParameters()[0]);
					if (TypeToken.of(AnalyserConfiguration.class).isAssignableFrom(type)) {
						Type configType = TypeUtils.resolve(f.getBaseType(), List.class.getTypeParameters()[0]);
						Type analysisType = TypeUtils.resolve(configType,
								AnalyserConfiguration.class.getTypeParameters()[0]);

						analysisType = TypeVisitor.accept(analysisType, new TypeVisitor<Type>() {
							@Override
							public Type visit(WildcardType v) {
								if (v.getUpperBounds().length == 1 && v.getLowerBounds().length == 0)
									return v.getUpperBounds()[0];
								else
									throw new Error("v=" + v);
							}

							@Override
							public <T> Type visit(Class<T> v) {
								return v;
							}
						});

						Set<Annotation> qualifiers = getQualifiers(f);
						qualifiers.add(configuredLiteratal);

						configurationBeans
								.add(createAnalyserListConfigurationBean(listOf(TypeToken.of(analysisType)).getType(),
										qualifiers, pat.getAnnotatedType().getJavaClass(), f, (Class) analysisType));
					} else {
						System.err.println("NOT CONFIG1 " + f.getJavaMember());
					}
				} else if (TypeToken.of(AnalyserConfiguration.class).isAssignableFrom(x.getRawType())) {

					TypeToken<?> analysisType = x.resolveType(AnalyserConfiguration.class.getTypeParameters()[0]);

					Set<Annotation> qualifiers = getQualifiers(f);
					qualifiers.add(configuredLiteratal);

					configurationBeans
							.add(createAnalyserConfigurationBean(analysisType.getType(), analysisType.getRawType(),
									qualifiers, pat.getAnnotatedType().getJavaClass(), f, analysisType.getRawType()));

				} else {
					System.err.println("NOT CONFIG0 " + f.getJavaMember());
				}
			}
		}

		if (pat.getAnnotatedType().isAnnotationPresent(AnaScope.class)) {
			typesAnaScope.add(pat.getAnnotatedType());
			pat.veto();
		}
	}

	private static Set<Annotation> getQualifiers(Annotated f) {
		Set<Annotation> qualifiers = new HashSet<>();
		for (Annotation a : f.getAnnotations())
			if (a.annotationType().isAnnotationPresent(Qualifier.class))
				qualifiers.add(a);
		return qualifiers;
	}

	private static <T> Bean<T> createBean(Class<? extends Annotation> scope, AnnotatedType<T> t) {
		return new Bean<T>() {

			@Override
			public T create(CreationalContext<T> creationalContext) {
				throw new Error();
			}

			@Override
			public void destroy(T instance, CreationalContext<T> creationalContext) {
				throw new Error();

			}

			@Override
			public Set<Type> getTypes() {
				return Sets.newHashSet(Object.class, t.getBaseType());
			}

			@Override
			public Set<Annotation> getQualifiers() {
				return Sets.newHashSet(defaultLiteratal);
			}

			@Override
			public Class<? extends Annotation> getScope() {
				return scope;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public Set<Class<? extends Annotation>> getStereotypes() {
				return Collections.emptySet();
			}

			@Override
			public boolean isAlternative() {
				return false;
			}

			@Override
			public Class<?> getBeanClass() {
				return (Class<?>) t.getJavaClass();
			}

			@Override
			public Set<InjectionPoint> getInjectionPoints() {
				return Collections.emptySet();
			}

			@Override
			public boolean isNullable() {
				throw new Error();
			}

			@Override
			public String toString() {
				return "ExtBean with type " + t.getJavaClass().getSimpleName() + " in scope " + scope.getSimpleName();
			}
		};

	}

	private static <T> ConfigurationBean<T> createAnalyserListConfigurationBean(Type type, Set<Annotation> qualifiers,
			Class<?> configurationClass, AnnotatedField<?> f, Class<?> targetClass) {
		return new AnalyserListConfigurationBean<T>() {

			@Override
			public T create(CreationalContext<T> creationalContext) {
				throw new Error();
			}

			@Override
			public void destroy(T instance, CreationalContext<T> creationalContext) {
				throw new Error();
			}

			@Override
			public Set<Type> getTypes() {
				return Sets.newHashSet(Object.class, type);
			}

			@Override
			public Set<Annotation> getQualifiers() {
				return qualifiers;
			}

			@Override
			public Class<? extends Annotation> getScope() {
				return Configuration.class;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public Set<Class<? extends Annotation>> getStereotypes() {
				return Collections.emptySet();
			}

			@Override
			public boolean isAlternative() {
				return false;
			}

			@Override
			public Class<?> getBeanClass() {
				return List.class;
			}

			@Override
			public Set<InjectionPoint> getInjectionPoints() {
				return Collections.emptySet();
			}

			@Override
			public boolean isNullable() {
				throw new Error();
			}

			@Override
			public String toString() {
				return "AnalyserListConfigurationBean [" + type + "]";
			}

			@Override
			public void accept(ConfigurationBeanVisitor visitor) {
				visitor.visit(this);
			}

			@Override
			public Class<?> getConfigurationClass() {
				return configurationClass;
			}

			@Override
			public List<?> extract(Object config) {
				try {
					f.getJavaMember().setAccessible(true);
					return (List<?>) f.getJavaMember().get(config);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public Class<?> getTargetClass() {
				return targetClass;
			}

		};

	}

	private static <T> ConfigurationBean<T> createAnalyserConfigurationBean(Type type, Class<T> clazz,
			Set<Annotation> qualifiers, Class<?> configurationClass, AnnotatedField<?> f, Class<?> targetClass) {
		return new AnalyserConfigurationBean<T>() {

			@Override
			public T create(CreationalContext<T> creationalContext) {
				throw new Error();
			}

			@Override
			public void destroy(T instance, CreationalContext<T> creationalContext) {
				throw new Error();
			}

			@Override
			public Set<Type> getTypes() {
				return Sets.newHashSet(Object.class, type);
			}

			@Override
			public Set<Annotation> getQualifiers() {
				return qualifiers;
			}

			@Override
			public Class<? extends Annotation> getScope() {
				return Configuration.class;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public Set<Class<? extends Annotation>> getStereotypes() {
				return Collections.emptySet();
			}

			@Override
			public boolean isAlternative() {
				return false;
			}

			@Override
			public Class<?> getBeanClass() {
				return clazz;
			}

			@Override
			public Set<InjectionPoint> getInjectionPoints() {
				return Collections.emptySet();
			}

			@Override
			public boolean isNullable() {
				throw new Error();
			}

			@Override
			public String toString() {
				return "AnaylserConfigurationBean [" + type + "]";
			}

			@Override
			public void accept(ConfigurationBeanVisitor visitor) {
				visitor.visit(this);
			}

			@Override
			public Class<?> getConfigurationClass() {
				return configurationClass;
			}

			@Override
			public Object extract(Object config) {
				try {
					f.getJavaMember().setAccessible(true);
					return f.getJavaMember().get(config);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public Class<?> getTargetClass() {
				return targetClass;
			}
		};

	}

	private static <T> ConfigurationBean<T> createConfigurationConfigurationBean(TypeToken<T> t) {
		return new ConfigurationConfigurationBean<T>() {

			@Override
			public T create(CreationalContext<T> creationalContext) {
				throw new Error();
			}

			@Override
			public void destroy(T instance, CreationalContext<T> creationalContext) {
				throw new Error();
			}

			@Override
			public Set<Type> getTypes() {
				return Sets.newHashSet(Object.class, t.getType());
			}

			@Override
			public Set<Annotation> getQualifiers() {
				return Sets.newHashSet(defaultLiteratal);
			}

			@Override
			public Class<? extends Annotation> getScope() {
				return Configuration.class;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public Set<Class<? extends Annotation>> getStereotypes() {
				return Collections.emptySet();
			}

			@Override
			public boolean isAlternative() {
				return false;
			}

			@Override
			public Class<?> getBeanClass() {
				return t.getRawType();
			}

			@Override
			public Set<InjectionPoint> getInjectionPoints() {
				return Collections.emptySet();
			}

			@Override
			public boolean isNullable() {
				throw new Error();
			}

			@Override
			public String toString() {
				return "ConfigurationBeanDirect [" + t + "]";
			}

			@Override
			public void accept(ConfigurationBeanVisitor visitor) {
				visitor.visit(this);
			}
		};

	}

	private void addOurScopeBeans(@Observes AfterBeanDiscovery abd) {
		abd.addContext(cc = new CC());
		abd.addContext(cc1 = new CC1());
		if (false)
			abd.addContext(cc2 = new CC2());

		for (AnnotatedType<?> t : typesAnaScope) {
			Bean<?> b = createBean(AnaScope.class, t);
			abd.addBean(b);
			beans.add(b);
		}

		configurationBeans.forEach(abd::addBean);
	}

	private Set<Bean<?>> beans = new HashSet<>();

	public static interface ConfigurationBeanVisitor {
		<T> void visit(ConfigurationConfigurationBean<T> bean);

		<T> void visit(AnalyserListConfigurationBean<T> bean);

		<T> void visit(AnalyserConfigurationBean<T> bean);

		<T> void visit(AnalyserListBean<T> bean);
	}

	public static interface ConfigurationBean<T> extends Bean<T> {
		void accept(ConfigurationBeanVisitor visitor);
	}

	public static interface AnalyserListBean<T> extends ConfigurationBean<T> {
		Class<?> getTargetClass();
	}

	public static interface ConfigurationConfigurationBean<T> extends ConfigurationBean<T> {
	}

	public static interface AnalyserConfigurationBean<T> extends ConfigurationBean<T> {
		Class<?> getConfigurationClass();

		Object extract(Object config);

		Class<?> getTargetClass();
	}

	public static interface AnalyserListConfigurationBean<T> extends ConfigurationBean<T> {
		Class<?> getConfigurationClass();

		List<?> extract(Object config);

		Class<?> getTargetClass();
	}

	public Collection<ConfigurationBean<?>> getDependencies(Bean<?> bean) {
		Collection<ConfigurationBean<?>> result = new LinkedList<>();
		beanDeps2.get(bean).forEach(result::add);
		return result;
	}
}
