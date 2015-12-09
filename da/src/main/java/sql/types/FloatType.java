package sql.types;

public class FloatType implements SqlType {
	private static final FloatType instance = new FloatType();

	private FloatType() {

	}

	public static FloatType create() {
		return instance;
	}
}
