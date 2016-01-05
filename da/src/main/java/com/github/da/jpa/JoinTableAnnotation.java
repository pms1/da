package com.github.da.jpa;

import java.util.List;

public class JoinTableAnnotation {
	public String schema;
	public String name;

	public List<JoinColumnAnnotation> joinColumns;
	public List<JoinColumnAnnotation> inverseJoinColumns;
}
