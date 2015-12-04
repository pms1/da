package com.github.da;

import java.util.Collections;
import java.util.List;

public class JoinColumnsAnnotation {
	public final List<JoinColumnAnnotation> value;

	public JoinColumnsAnnotation(List<JoinColumnAnnotation> value) {
		this.value = Collections.unmodifiableList(value);
	}
}
