package sql.types;

public class CLOBType implements SqlType {
	private static final CLOBType instance = new CLOBType();

	private CLOBType() {

	}

	public static CLOBType create() {
		return instance;
	}
}
