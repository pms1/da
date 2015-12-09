package sql.types;

public class VarbinaryType implements SqlType {
	private static final VarbinaryType instance = new VarbinaryType();

	private VarbinaryType() {

	}

	public static VarbinaryType create() {
		return instance;
	}
}
