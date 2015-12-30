package com.github.da;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
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
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.github.da.t.AD;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class Ext implements Extension {

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
				throw new Error("Unbound " + contextual);
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
			System.err.println("C " + contextual);
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

	static class DefaultAnn extends AnnotationLiteral<Default> {

	}

	private Set<AnnotatedType<?>> typesAnaScope = new HashSet<>();
	private Set<AnnotatedType<?>> typesConfiguration = new HashSet<>();

	private Set<Type> typesConfiguration2 = new HashSet<>();

	public boolean isConfiguration(Type type) {
		return typesConfiguration2.contains(type);
	}

	private Set<IndirectData> t = new HashSet<>();

	static <T> TypeToken<List<T>> listOf(TypeToken<T> t) {
		return new TypeToken<List<T>>() {
		}.where(new TypeParameter<T>() {
		}, t);
	}

	static class Dep {
		private final TypeToken<?> type;

		Dep(TypeToken<?> type) {
			this.type = type;
		}
	}

	static class DirectDep extends Dep {

		DirectDep(TypeToken<?> type) {
			super(type);
		}

	}

	static class IndirectDep extends Dep {

		IndirectDep(TypeToken<?> type) {
			super(type);
			// TODO Auto-generated constructor stub
		}

	}

	private Multimap<TypeToken<?>, Dep> deps = HashMultimap.create();

	private Multimap<Bean<?>, Dep> beanDeps = HashMultimap.create();

	private <X> void processBean(@Observes ProcessBean<X> b) {
		for (InjectionPoint ip : b.getBean().getInjectionPoints()) {
			TypeToken<?> tt = TypeToken.of(ip.getType());

			for (Dep d : deps.get(tt))
				beanDeps.put(b.getBean(), d);
		}
	}

	private <X> void vetoOurScopeTypes(@Observes ProcessAnnotatedType<X> pat) {
		if (pat.getAnnotatedType().isAnnotationPresent(Configuration.class)) {
			typesConfiguration.add(pat.getAnnotatedType());
			typesConfiguration2.add(pat.getAnnotatedType().getBaseType());
			pat.veto();

			deps.put(TypeToken.of(pat.getAnnotatedType().getBaseType()),
					new DirectDep(TypeToken.of(pat.getAnnotatedType().getBaseType())));

			for (AnnotatedField<? super X> f : pat.getAnnotatedType().getFields()) {
				TypeToken<?> x = TypeToken.of(f.getBaseType());
				if (x.getRawType().equals(List.class)) {
					TypeToken<?> type = x.resolveType(List.class.getTypeParameters()[0]);
					if (TypeToken.of(AD.class).isAssignableFrom(type)) {
						TypeToken<?> token = type.resolveType(AD.class.getTypeParameters()[0]);

						TypeToken t2 = listOf(token);

						deps.put(t2, new IndirectDep(t2));

						IndirectData id = new IndirectData();
						id.beanType = t2;
						id.configurationClass = pat.getAnnotatedType().getJavaClass();
						id.field = f;
						id.targetClass = token.getRawType();
						t.add(id);
					}
				}
			}
		}
		if (pat.getAnnotatedType().isAnnotationPresent(AnaScope.class)) {
			typesAnaScope.add(pat.getAnnotatedType());
			pat.veto();
		}
	}

	static private class IndirectData {
		AnnotatedField<?> field;
		TypeToken<?> beanType;
		Class<?> configurationClass;
		protected Class<?> targetClass;
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
				return Sets.newHashSet(new DefaultAnn());
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

	private static <T> ConfigurationBean<T> createBean2(IndirectData id) {
		return new ConfigurationBeanIndirect<T>() {

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
				return Sets.newHashSet(Object.class, id.beanType.getType());
			}

			@Override
			public Set<Annotation> getQualifiers() {
				return Sets.newHashSet(new DefaultAnn());
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
				return id.beanType.getRawType();
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
				return "ConfigurationBeanIndirect [" + id.beanType + "]";
			}

			@Override
			public void accept(ConfigurationBeanVisitor visitor) {
				visitor.visit(this);
			}

			@Override
			public Class<?> getConfigurationClass() {
				return id.configurationClass;
			}

			@Override
			public List<?> extract(Object config) {
				try {
					return (List) id.field.getJavaMember().get(config);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public Class<?> getTargetClass() {
				return id.targetClass;
			}
		};

	}

	private static <T> ConfigurationBean<T> createBean3(TypeToken<T> t) {
		return new ConfigurationBeanDirect<T>() {

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
				return Sets.newHashSet(new DefaultAnn());
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
				return "ExtBean3 with type " + t;
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

		for (AnnotatedType<?> t : typesAnaScope) {
			Bean<?> b = createBean(AnaScope.class, t);
			abd.addBean(b);
			beans.add(b);
		}

		for (AnnotatedType<?> t : typesConfiguration) {
			ConfigurationBean<?> b = createBean3(TypeToken.of(t.getBaseType()));
			abd.addBean(b);
			beans2.put(TypeToken.of(t.getBaseType()), b);
		}

		for (IndirectData t1 : t) {
			ConfigurationBean<?> b = createBean2(t1);
			abd.addBean(b);
			beans2.put(t1.beanType, b);
		}
	}

	private Map<TypeToken<?>, ConfigurationBean<?>> beans2 = new HashMap<>();

	private Set<Bean<?>> beans = new HashSet<>();

	public static interface ConfigurationBeanVisitor {
		<T> void visit(ConfigurationBeanDirect<T> bean);

		<T> void visit(ConfigurationBeanIndirect<T> bean);
	}

	public static interface ConfigurationBean<T> extends Bean<T> {
		void accept(ConfigurationBeanVisitor visitor);
	}

	public static interface ConfigurationBeanDirect<T> extends ConfigurationBean<T> {
	}

	public static interface ConfigurationBeanIndirect<T> extends ConfigurationBean<T> {
		Class<?> getConfigurationClass();

		List<?> extract(Object config);

		Class<?> getTargetClass();
	}

	public Collection<ConfigurationBean<?>> getDependencies(Bean<?> bean) {
		Collection<ConfigurationBean<?>> result = new LinkedList<>();
		for (Dep e : beanDeps.get(bean)) {
			result.add(resolve(e));
		}
		return result;
	}

	private ConfigurationBean<?> resolve(Dep e) {
		ConfigurationBean<?> bean = beans2.get(e.type);
		if (bean == null)
			throw new Error("unresolved " + bean);
		return bean;
	}
}
