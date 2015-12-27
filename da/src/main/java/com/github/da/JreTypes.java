package com.github.da;

import org.objectweb.asm.Type;

class JreTypes {
	static final Type javaLangObject = Type.getObjectType("java/lang/Object");

	static final Type javaLangBoolean = Type.getObjectType("java/lang/Boolean");
	static final Type javaLangByte = Type.getObjectType("java/lang/Byte");
	static final Type javaLangCharacter = Type.getObjectType("java/lang/Character");
	static final Type javaLangEnum = Type.getObjectType("java/lang/Enum");
	static final Type javaLangDouble = Type.getObjectType("java/lang/Double");
	static final Type javaLangFloat = Type.getObjectType("java/lang/Float");
	static final Type javaLangInteger = Type.getObjectType("java/lang/Integer");
	static final Type javaLangLong = Type.getObjectType("java/lang/Long");
	static final Type javaLangString = Type.getObjectType("java/lang/String");

	static final Type javaMathBigDecimal = Type.getObjectType("java/math/BigDecimal");

	static final Type javaUtilDate = Type.getObjectType("java/util/Date");

	static final Type javaSqlDate = Type.getObjectType("java/sql/Date");
	static final Type javaSqlTime = Type.getObjectType("java/sql/Time");

}