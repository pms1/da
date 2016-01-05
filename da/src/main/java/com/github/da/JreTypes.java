package com.github.da;

import org.objectweb.asm.Type;

public class JreTypes {
	private JreTypes() {

	}

	public static final Type javaLangObject = Type.getObjectType("java/lang/Object");

	public static final Type javaLangBoolean = Type.getObjectType("java/lang/Boolean");
	public static final Type javaLangByte = Type.getObjectType("java/lang/Byte");
	public static final Type javaLangCharacter = Type.getObjectType("java/lang/Character");
	public static final Type javaLangEnum = Type.getObjectType("java/lang/Enum");
	public static final Type javaLangDouble = Type.getObjectType("java/lang/Double");
	public static final Type javaLangFloat = Type.getObjectType("java/lang/Float");
	public static final Type javaLangInteger = Type.getObjectType("java/lang/Integer");
	public static final Type javaLangLong = Type.getObjectType("java/lang/Long");
	public static final Type javaLangString = Type.getObjectType("java/lang/String");

	public static final Type javaMathBigDecimal = Type.getObjectType("java/math/BigDecimal");

	public static final Type javaUtilDate = Type.getObjectType("java/util/Date");

	public static final Type javaSqlDate = Type.getObjectType("java/sql/Date");
	public static final Type javaSqlTime = Type.getObjectType("java/sql/Time");

}