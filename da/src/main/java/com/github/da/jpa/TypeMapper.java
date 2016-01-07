package com.github.da.jpa;

import com.github.da.ClassLoader;

import sql.types.SqlType;

public interface TypeMapper {

	SqlType map(ClassLoader ch, JpaProperty p);

}
