package com.github.da.t;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

public class A2 {
	@Inject
	A2Config config;

	@PostConstruct
	void pc() {
		System.err.println(this + " postConstruct");
	}

	@PreDestroy
	void pd() {
		System.err.println(this + " preDestroy");
	}

	@Override
	public String toString() {
		return super.toString() + "(" + config + ")";
	}
}
