package com.github.da.jpa;

import java.util.Collections;
import java.util.List;

public class AttributeOverridesAnnotation {
	public final List<AttributeOverrideAnnotation> value;

	public AttributeOverridesAnnotation(List<AttributeOverrideAnnotation> value) {
		this.value = Collections.unmodifiableList(value);
	}
}
