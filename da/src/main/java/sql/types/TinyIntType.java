package sql.types;

public class TinyIntType implements SqlType {
	private static final TinyIntType instance = new TinyIntType();

	private TinyIntType() {

	}

	public static TinyIntType create() {
		return instance;
	}
}
