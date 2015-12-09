package sql.types;

public class DecimalType implements SqlType {
	private static final DecimalType instance = new DecimalType();

	private DecimalType() {

	}

	public static DecimalType create() {
		return instance;
	}
}
