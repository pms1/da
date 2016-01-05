package sql;

import javax.enterprise.inject.Vetoed;

@Vetoed
public interface SchemaIdVisitor<T> {
	default T visitAnonymous() {
		return null;
	}

	default T visitName(String name) {
		return null;
	}
}
