package sql.types;

public class VarcharType implements SqlType {
	private static final VarcharType instance = new VarcharType();

	private VarcharType() {

	}

	public static VarcharType create() {
		return instance;
	}
}
