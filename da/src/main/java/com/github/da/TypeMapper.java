package com.github.da;

import sql.types.SqlType;

public interface TypeMapper {

	default SqlType map(ClassHierarchy2 ch, JpaProperty p) {
		return map((ClassHierarchy) null, p);
	}

	SqlType map(ClassHierarchy ch, JpaProperty p);

}
