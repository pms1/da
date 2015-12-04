package com.github.da;

import java.util.List;

@AnnotationDataObject
public class OneToOneAnnotation {

	public String mappedBy;
	public Boolean orphanRemoval;
	public List<JpaCascadeType> cascade;
	public JpaFetchType fetch;

}
