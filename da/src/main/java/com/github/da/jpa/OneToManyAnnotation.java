package com.github.da.jpa;

import java.util.List;

public class OneToManyAnnotation {

	public String mappedBy;
	public Boolean orphanRemoval;
	public List<JpaCascadeType> cascade;
	public JpaFetchType fetch;
}
