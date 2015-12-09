package da;

public class H2ModelReader extends ModelReader {

	@Override
	boolean ignore(String c, String s, String n) {
		return s.equals("INFORMATION_SCHEMA");
	}

}
