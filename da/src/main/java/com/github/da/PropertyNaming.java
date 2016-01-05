package com.github.da;

public class PropertyNaming {
	public static String toProperty(String name) {
		if (name.startsWith("get") || name.startsWith("set")) {
			name = name.substring(3);
			// if (!Character.isUpperCase(name.charAt(0)))
			// throw new IllegalArgumentException("name=" + name);
			return Character.toLowerCase(name.charAt(0)) + name.substring(1);
		} else {
			throw new IllegalArgumentException("name=" + name);
		}
	}
}
