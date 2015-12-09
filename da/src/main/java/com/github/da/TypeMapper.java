package com.github.da;

import sql.types.SqlType;

public interface TypeMapper {

	SqlType map(ClassHierarchy ch, JpaProperty p);

}
