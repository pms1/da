package sql.types;

public class SmallIntType implements SqlType {
	private static final SmallIntType instance = new SmallIntType();

	private SmallIntType() {

	}

	public static SmallIntType create() {
		return instance;
	}
}
