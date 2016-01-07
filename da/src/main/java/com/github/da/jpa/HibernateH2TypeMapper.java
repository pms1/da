package com.github.da.jpa;

import com.github.da.ClassLoader;
import com.github.da.JreTypes;

import asm.BaseType;
import asm.JavaType;
import asm.JavaTypeVisitor;
import asm.RawType;
import sql.types.BooleanType;
import sql.types.SqlType;
import sql.types.TinyIntType;

public class HibernateH2TypeMapper implements TypeMapper {

	@Override
	public SqlType map(ClassLoader ch, JpaProperty p) {
		return p.type2.accept(new JavaTypeVisitor<SqlType>() {

			@Override
			public SqlType visitBoolean(BaseType baseType) {
				return BooleanType.create();
			}

			@Override
			public SqlType visitByte(BaseType baseType) {
				return TinyIntType.create();
			}

			@Override
			public SqlType visit(RawType rawType) {
				if (rawType.getRawType().equals(JreTypes.javaLangBoolean)) {
					return BooleanType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaLangByte)) {
					return TinyIntType.create();
				} else {
					return null;
				}
			}

			@Override
			public SqlType visit(JavaType javaType) {
				return null;
			}

		});

	}

}
