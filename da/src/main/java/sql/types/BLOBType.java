package sql.types;

public class BLOBType implements SqlType {
	private static final BLOBType instance = new BLOBType();

	private BLOBType() {

	}

	public static BLOBType create() {
		return instance;
	}
}
