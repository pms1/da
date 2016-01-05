package com.github.da.jpa;

import java.util.Map;
import java.util.Objects;

import com.github.da.ClassData;
import com.github.da.JpaAccess;

public class JpaAnalysisResult2 {
	public final ClassData clazz;

	JpaAnalysisResult2(ClassData clazz) {
		Objects.requireNonNull(clazz);
		this.clazz = clazz;
	}

	private JpaAccess access;

	public void setAccess(JpaAccess access) {
		Objects.requireNonNull(access);
		if (this.access != null)
			throw new IllegalStateException();
		this.access = access;
	}

	public JpaAccess getAccess() {
		return access;
	}

	private boolean isEntity;

	public void setEntity() {
		isEntity = true;
	}

	public boolean isEntity() {
		return isEntity;
	}

	private boolean isEmbeddable;

	public void setEmbeddable() {
		isEmbeddable = true;
	}

	boolean isEmbeddable() {
		return isEmbeddable;
	}

	private boolean isMappedSuperclass;

	public void setMappedSuperclass() {
		isMappedSuperclass = true;
	}

	boolean isMappedSuperclass() {
		return isMappedSuperclass;
	}

	// private JpaKind kind;
	//
	// public void setKind(JpaKind kind) {
	// Objects.requireNonNull(kind);
	// if (this.kind != null)
	// throw new IllegalStateException();
	// this.kind = kind;
	// }
	//
	// public JpaKind getKind() {
	// return kind;
	// }

	public Map<String, JpaProperty> properties;

	private TableAnnotation table;

	public void setTable(TableAnnotation table) {
		Objects.requireNonNull(table);
		if (this.table != null)
			throw new IllegalStateException();
		this.table = table;
	}

	public TableAnnotation getTable() {
		return table;
	}
}
