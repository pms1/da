package sql.types;

public class IntType implements SqlType {
	private static final IntType instance = new IntType();

	private IntType() {

	}

	public static IntType create() {
		return instance;
	}
}
