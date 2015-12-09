package sql.types;

public class DoubleType implements SqlType {
	private static final DoubleType instance = new DoubleType();

	private DoubleType() {

	}

	public static DoubleType create() {
		return instance;
	}
}
