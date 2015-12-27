package com.github.da;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.github.da.Ext.CC;
import com.github.naf.Application;
import com.github.naf.ApplicationBuilder;
import com.google.common.collect.Iterables;

public class DeploymentAnalyserMain2 {
	// @ApplicationScoped
	static class Sample1 {
		AnalysisResult ar;
		
		@Inject
		void in(AnalysisResult ar) {
			System.out.println("in " + this + " " + ar);
			this.ar = ar;
		}
		@PostConstruct
		public void pc() {
			System.out.println("pc " + this + " "  + ar);
		}
		@PreDestroy
		public void pd() {
			System.out.println("pd " + this + " " + ar);
		}
		
		@Override
		public String toString() {
			return super.toString() + "(" + ar + ")";
		}
	}
	public static void main(String[] args) {
		AnalysisConfiguration ac = new AnalysisConfiguration();
		try(Application a = new ApplicationBuilder().with(new ConfigurationExtension(ac)).with(new Ext()).build()) {
			
			if(true){
			BeanManager bm = a.get(BeanManager.class);
			Bean<Sample1> bean = (Bean<Sample1>) Iterables.getOnlyElement(bm.getBeans(Sample1.class));
		
			for(Object o :bean.getInjectionPoints())
				System.out.println("IP " + o);
			
			CreationalContext<Sample1> context = bm.createCreationalContext(bean);
			System.out.println("1.1");
			CC cc = a.get(CC.class);
			cc.activate(new AnalysisResult("0"));
				Object reference = bm.getReference(bean, bean.getBeanClass(), context);
				System.out.println("1.3 " + reference);
				Object reference1 = bm.getReference(bean, bean.getBeanClass(), context);
				System.out.println("1.3 " + reference1);
						cc.deactivate();
						System.out.println("1.2 " + reference);
						context.release();
			}
			
			System.out.println(1);
			CC cc = a.get(CC.class);
			cc.activate(new AnalysisResult("1"));
			Sample1 s = a.get(Sample1.class);
			System.out.println("5 " + s);
			cc.deactivate();
			System.out.println("6 " + s);
			System.out.println(4);
			cc.activate(new AnalysisResult("2"));
			s = a.get(Sample1.class);
			System.out.println("7 " + s);
						cc.deactivate();
						System.out.println("8 " + s);
			System.out.println(2);
			
		}
		System.out.println(3);

	}
}
