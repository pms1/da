package sql.types;

public class LongVarcharType implements SqlType {
	private static final LongVarcharType instance = new LongVarcharType();

	private LongVarcharType() {

	}

	public static LongVarcharType create() {
		return instance;
	}
}
