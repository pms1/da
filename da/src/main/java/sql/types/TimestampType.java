package sql.types;

public class TimestampType implements SqlType {
	private static final TimestampType instance = new TimestampType();

	private TimestampType() {

	}

	public static TimestampType create() {
		return instance;
	}
}
