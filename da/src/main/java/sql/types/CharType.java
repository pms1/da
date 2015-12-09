package sql.types;

public class CharType implements SqlType {
	private static final CharType instance = new CharType();

	private CharType() {

	}

	public static CharType create() {
		return instance;
	}
}
