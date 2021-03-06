package pkg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.google.common.collect.Sets;

public class Ext implements Extension {

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

			if (beanClass.equals(AnaResult.class))
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

		AnaResult current;

		public void activate(AnaResult aResult) {
			Objects.requireNonNull(aResult);
			current = aResult;
		}

		public void deactivate() {
			current = null;
		}

		Map<Class<?>, Object> bindings = new HashMap<>();

		public void bind(Class<? extends Object> key, Object value) {
			if (bindings.putIfAbsent(key, value) != null)
				throw new IllegalArgumentException();
		}

		public void unbind(Class<?> key) {
			if (bindings.remove(key) == null)
				throw new IllegalArgumentException();
		}
	}

	CC cc;

	CC getCC() {
		return cc;
	}

	@ApplicationScoped
	static class OurParametersFactory {
		@Inject
		Ext e;

		@Produces
		CC foo() {
			return e.getCC();
		}
	}

	static class DefaultAnn extends AnnotationLiteral<Default> {

	}

	private Set<AnnotatedType<?>> types = new HashSet<>();

	private <X> void vetoOurScopeTypes(@Observes ProcessAnnotatedType<X> pat) {
		if (pat.getAnnotatedType().isAnnotationPresent(AnaScope.class)) {
			types.add(pat.getAnnotatedType());
			pat.veto();
		}
	}

	private <X> void pit(@Observes ProcessInjectionTarget<X> pit) {
		if (!pit.getAnnotatedType().getJavaClass().getSimpleName().startsWith("T"))
			return;

		System.err.println("PIT " + pit.getInjectionTarget());
		for (InjectionPoint ip : pit.getInjectionTarget().getInjectionPoints()) {
			System.err.println("  PIP " + ip);
		}
	}

	private <X> void pb(@Observes ProcessBean<X> pb) {
		System.err.println("PB " + pb.getBean());
		for (InjectionPoint ip : pb.getBean().getInjectionPoints()) {
			System.err.println("  PIP " + ip);
		}
	}

	private void addOurScopeBeans(@Observes AfterBeanDiscovery abd) {
		abd.addContext(cc = new CC());

		for (AnnotatedType<?> t : types) {

			Bean<?> b = new Bean<Object>() {

				@Override
				public Object create(CreationalContext<Object> creationalContext) {
					throw new Error();
				}

				@Override
				public void destroy(Object instance, CreationalContext<Object> creationalContext) {
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
					return AnaScope.class;
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

			};
			abd.addBean(b);
			beans.add(b);
		}
	}

	private Set<Bean<?>> beans = new HashSet<>();
}
