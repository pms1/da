package sql;

public interface SchemaIdVisitor<T> {
	default T visitAnonymous() {
		return null;
	}

	default T visitName(String name) {
		return null;
	}
}
