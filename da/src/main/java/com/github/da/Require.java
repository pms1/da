package com.github.da;

import java.lang.annotation.Repeatable;

@Repeatable(Requires.class)
public @interface Require {

	Class<?> value();

}
