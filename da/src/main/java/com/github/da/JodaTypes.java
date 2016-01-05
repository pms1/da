package com.github.da;

import org.objectweb.asm.Type;

public class JodaTypes {
	private JodaTypes() {

	}

	public static final Type orgJodaTimeDateTime = Type.getObjectType("org/joda/time/DateTime");
	public static final Type orgJodaTimeLocalDate = Type.getObjectType("org/joda/time/LocalDate");
	public static final Type orgJodaTimeLocalTime = Type.getObjectType("org/joda/time/LocalTime");
}