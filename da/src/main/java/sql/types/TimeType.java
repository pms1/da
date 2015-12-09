package sql.types;

public class TimeType implements SqlType {
	private static final TimeType instance = new TimeType();

	private TimeType() {

	}

	public static TimeType create() {
		return instance;
	}
}
