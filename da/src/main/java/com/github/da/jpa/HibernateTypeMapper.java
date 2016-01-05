package com.github.da.jpa;

import java.util.Objects;

import com.github.da.ClassData;
import com.github.da.ClassHierarchy2;
import com.github.da.JodaTypes;
import com.github.da.JreTypes;
import com.github.da.TypeMapper;

import asm.ArrayType;
import asm.BaseType;
import asm.ClassSignature;
import asm.JavaType;
import asm.JavaTypeVisitor;
import asm.RawType;
import sql.types.BLOBType;
import sql.types.BigIntType;
import sql.types.CLOBType;
import sql.types.CharType;
import sql.types.DateType;
import sql.types.DecimalType;
import sql.types.DoubleType;
import sql.types.IntType;
import sql.types.SqlType;
import sql.types.TimeType;
import sql.types.TimestampType;
import sql.types.VarbinaryType;
import sql.types.VarcharType;

public class HibernateTypeMapper implements TypeMapper {

	@Override
	public SqlType map(ClassHierarchy2 ch, JpaProperty p) {
		Objects.requireNonNull(ch);
		Objects.requireNonNull(p);

		return p.type2.accept(new JavaTypeVisitor<SqlType>() {

			@Override
			public SqlType visit(ArrayType arrayType) {
				return arrayType.getContentType().accept(new JavaTypeVisitor<SqlType>() {
					@Override
					public SqlType visitByte(BaseType baseType) {
						if (Boolean.TRUE.equals(p.lob))
							return BLOBType.create();
						else
							return VarbinaryType.create();
					}

					@Override
					public SqlType visitChar(BaseType baseType) {
						if (Boolean.TRUE.equals(p.lob))
							return CLOBType.create();
						else
							return VarcharType.create();
					}
				});
			}

			@Override
			public SqlType visitChar(BaseType baseType) {
				return CharType.create();
			}

			@Override
			public SqlType visitFloat(BaseType baseType) {
				// FIXME: driver
				return DoubleType.create();
			}

			@Override
			public SqlType visitDouble(BaseType baseType) {
				return DoubleType.create();
			}

			@Override
			public SqlType visitInt(BaseType baseType) {
				return IntType.create();
			}

			@Override
			public SqlType visitLong(BaseType baseType) {
				return BigIntType.create();
			}

			@Override
			public SqlType visit(RawType rawType) {
				if (rawType.getRawType().equals(JreTypes.javaLangCharacter)) {
					return CharType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaLangFloat)) {
					// FIXME: driver
					return DoubleType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaLangDouble)) {
					return DoubleType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaLangInteger)) {
					return IntType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaLangLong)) {
					return BigIntType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaLangString)) {
					if (Boolean.TRUE.equals(p.lob))
						return CLOBType.create();
					else
						return VarcharType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaSqlDate)) {
					return DateType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaSqlTime)) {
					return TimeType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaMathBigDecimal)) {
					return DecimalType.create();
				} else if (rawType.getRawType().equals(JreTypes.javaUtilDate)) {
					JpaTemporalType t = p.temporalType != null ? p.temporalType : JpaTemporalType.TIMESTAMP;
					switch (t) {
					case DATE:
						return DateType.create();
					case TIME:
						return TimeType.create();
					case TIMESTAMP:
						return TimestampType.create();
					default:
						throw new Error();
					}
				} else if (rawType.getRawType().equals(JodaTypes.orgJodaTimeDateTime)) {
					return TimestampType.create();
				} else if (rawType.getRawType().equals(JodaTypes.orgJodaTimeLocalDate)) {
					return DateType.create();
				} else if (rawType.getRawType().equals(JodaTypes.orgJodaTimeLocalTime)) {
					return TimeType.create();
				} else {
					ClassData classModel = ch.get(rawType.getRawType());
					if (classModel != null && classModel.get(ClassSignature.class).getSuperclass().getRawType()
							.equals(JreTypes.javaLangEnum)) {
						if (p.enumType == null || p.enumType == JpaEnumType.ORDINAL)
							return IntType.create();
						else
							return VarcharType.create();
					}

					System.err.println("MISSING TYPE " + rawType);
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
