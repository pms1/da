package sql.types;

public class DateType implements SqlType {
	private static final DateType instance = new DateType();

	private DateType() {

	}

	public static DateType create() {
		return instance;
	}
}
