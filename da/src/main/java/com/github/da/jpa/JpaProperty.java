package com.github.da.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.objectweb.asm.Type;

import com.github.da.JpaAccess;

import asm.JavaType;

public class JpaProperty {
	JpaProperty(String name) {
		this.name = name;
	}

	public final String name;

	JpaAccess access;

	void setAccess(JpaAccess access) {
		Objects.requireNonNull(access);
		if (this.access != null)
			throw new IllegalStateException();
		this.access = access;
	}

	private CollectionTableAnnotation collectionTable;

	void setCollectionTable(CollectionTableAnnotation collectionTable) {
		Objects.requireNonNull(collectionTable);
		if (this.collectionTable != null)
			throw new IllegalStateException();
		this.collectionTable = collectionTable;
	}

	public CollectionTableAnnotation getCollectionTable() {
		return collectionTable;
	}

	public ColumnAnnotation column;

	public void setColumn(ColumnAnnotation column) {
		Objects.requireNonNull(column);
		if (this.column != null)
			throw new IllegalStateException();
		this.column = column;
	}

	public OneToOneAnnotation onetoone;

	public void setOneToOne(OneToOneAnnotation onetoone) {
		Objects.requireNonNull(onetoone);
		if (this.onetoone != null)
			throw new IllegalStateException();
		this.onetoone = onetoone;
	}

	public OneToManyAnnotation onetomany;

	public void setOneToMany(OneToManyAnnotation onetomany) {
		Objects.requireNonNull(onetomany);
		if (this.onetomany != null)
			throw new IllegalStateException();
		this.onetomany = onetomany;
	}

	ManyToOneAnnotation manytoone;

	public void setManyToOne(ManyToOneAnnotation manytoone) {
		Objects.requireNonNull(manytoone);
		if (this.manytoone != null)
			throw new IllegalStateException();
		this.manytoone = manytoone;
	}

	public ManyToManyAnnotation manytomany;

	public void setManyToMany(ManyToManyAnnotation manytomany) {
		Objects.requireNonNull(manytomany);
		if (this.manytomany != null)
			throw new IllegalStateException();
		this.manytomany = manytomany;
	}

	Map<String, ColumnAnnotation> attributeOverrides;

	public void setAttributeOverrides(Map<String, ColumnAnnotation> attributeOverrides) {
		Objects.requireNonNull(attributeOverrides);
		if (this.attributeOverrides != null)
			throw new IllegalStateException();
		this.attributeOverrides = attributeOverrides;
	}

	public List<JoinColumnAnnotation> joinColumns;

	public void setJoinColumns(List<JoinColumnAnnotation> joinColumns) {
		Objects.requireNonNull(joinColumns);
		if (this.joinColumns != null)
			throw new IllegalStateException();
		this.joinColumns = joinColumns;
	}

	FieldType fieldType;

	void setFieldType(FieldType fieldType) {
		Objects.requireNonNull(fieldType);
		if (this.fieldType != null)
			throw new IllegalStateException();
		this.fieldType = fieldType;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	JpaEnumType enumType;

	void setEnumType(JpaEnumType enumType) {
		Objects.requireNonNull(enumType);
		if (this.enumType != null)
			throw new IllegalStateException();
		this.enumType = enumType;
	}

	JpaTemporalType temporalType;

	void setTemporalType(JpaTemporalType temporalType) {
		Objects.requireNonNull(temporalType);
		if (this.temporalType != null)
			throw new IllegalStateException();
		this.temporalType = temporalType;
	}

	public OrderColumnAnnotation orderColumn;

	void setOrderColumn(OrderColumnAnnotation orderColumn) {
		Objects.requireNonNull(orderColumn);
		if (this.orderColumn != null)
			throw new IllegalStateException();
		this.orderColumn = orderColumn;
	}

	public JoinTableAnnotation joinTable;

	void setJoinTable(JoinTableAnnotation joinTable) {
		Objects.requireNonNull(joinTable);
		if (this.joinTable != null)
			throw new IllegalStateException();
		this.joinTable = joinTable;
	}

	Boolean lob;

	void setLob(Boolean lob) {
		Objects.requireNonNull(lob);
		if (this.lob != null)
			throw new IllegalStateException();
		this.lob = lob;
	}

	public Type type;

	public void setType(Type type) {
		Objects.requireNonNull(type);
		if (this.type != null)
			throw new IllegalStateException();
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public JavaType type2;

	public void setType2(JavaType type2) {
		Objects.requireNonNull(type2);
		if (this.type2 != null)
			throw new IllegalStateException();
		this.type2 = type2;
	}

	@Override
	public String toString() {
		return "JpaProperty(" + name + ")";
	}

	public boolean id = false;

	public void setId(boolean id) {
		this.id = id;
	}

	boolean trans = false;

	public void setTransient(boolean trans) {
		this.trans = trans;
	}

	public Collection<JpaProperty> collectionTableProperties;

	public Type elementType;

	JpaProperty withColumn(ColumnAnnotation column) {
		JpaProperty result = withName(name);
		result.column = column;
		return result;
	}

	JpaProperty withName(String name) {
		JpaProperty result = new JpaProperty(name);
		result.access = access;
		result.attributeOverrides = attributeOverrides;
		result.collectionTable = collectionTable;
		if (collectionTableProperties != null)
			result.collectionTableProperties = new ArrayList<>(collectionTableProperties);
		result.column = column;
		result.elementType = elementType;
		result.enumType = enumType;
		result.fieldType = fieldType;
		result.id = id;
		result.joinColumns = joinColumns;
		result.joinTable = joinTable;
		result.manytomany = manytomany;
		result.manytoone = manytoone;
		result.onetomany = onetomany;
		result.onetoone = onetoone;
		result.orderColumn = orderColumn;
		result.temporalType = temporalType;
		result.trans = trans;
		result.type = type;
		result.type2 = type2;
		return result;
	}
}
