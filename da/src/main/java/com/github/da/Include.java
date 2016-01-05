package com.github.da;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(Includes.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface Include {
	Class<?/* extends Analyser<?> */>value();
}
