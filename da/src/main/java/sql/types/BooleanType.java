package sql.types;

public class BooleanType implements SqlType {
	private static final BooleanType instance = new BooleanType();

	private BooleanType() {

	}

	public static BooleanType create() {
		return instance;
	}
}
