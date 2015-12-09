package sql.types;

public class BigIntType implements SqlType {
	private static final BigIntType instance = new BigIntType();

	private BigIntType() {

	}

	public static BigIntType create() {
		return instance;
	}
}
