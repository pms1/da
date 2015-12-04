package com.github.da;

public class AttributeOverrideAnnotation {
	public AttributeOverrideAnnotation(String name, ColumnAnnotation column) {
		this.name = name;
		this.column = column;
	}

	public final String name;
	public final ColumnAnnotation column;
}
