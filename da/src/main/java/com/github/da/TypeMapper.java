package com.github.da;

import sql.types.SqlType;

public interface TypeMapper {

	SqlType map(ClassHierarchy2 ch, JpaProperty p);

}
