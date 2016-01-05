package com.github.da;

import asm.BaseType;
import asm.JavaType;
import asm.JavaTypeVisitor;
import asm.RawType;
import sql.types.SmallIntType;
import sql.types.SqlType;

public class HibernateDB2TypeMapper implements TypeMapper {

	@Override
	public SqlType map(ClassHierarchy2 ch, JpaProperty p) {
		return p.type2.accept(new JavaTypeVisitor<SqlType>() {

			@Override
			public SqlType visitBoolean(BaseType baseType) {
				return SmallIntType.create();
			}

			@Override
			public SqlType visitByte(BaseType baseType) {
				return SmallIntType.create();
			}

			@Override
			public SqlType visit(JavaType javaType) {
				return null;
			}

			@Override
			public SqlType visit(RawType rawType) {
				if (rawType.getRawType().equals(JreTypes.javaLangBoolean)) {
					return SmallIntType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaLangByte)) {
					return SmallIntType.create();
				} else {
					return null;
				}
			}

		});

	}

}
