package com.github.da;

public enum JpaCascadeType {
	/** Cascade all operations */
	ALL,

	/** Cascade persist operation */
	PERSIST,

	/** Cascade merge operation */
	MERGE,

	/** Cascade remove operation */
	REMOVE,

	/** Cascade refresh operation */
	REFRESH,

	/**
	 * Cascade detach operation
	 *
	 * @since Java Persistence 2.0
	 *
	 */
	DETACH
}
