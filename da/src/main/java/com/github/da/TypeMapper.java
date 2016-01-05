package com.github.da;

import com.github.da.jpa.JpaProperty;

import sql.types.SqlType;

public interface TypeMapper {

	SqlType map(ClassHierarchy2 ch, JpaProperty p);

}
