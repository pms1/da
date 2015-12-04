package sql;

import java.util.Objects;

public class TableId {
	private final SchemaId schema;
	private final String name;

	private TableId(SchemaId schema, String name) {
		Objects.requireNonNull(schema);
		this.schema = schema;
		Objects.requireNonNull(name);
		if (name.isEmpty())
			throw new IllegalArgumentException();
		this.name = name;
	}

	public static TableId create(SchemaId schema, String name) {
		return new TableId(schema, name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(schema, name.toLowerCase());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableId other = (TableId) obj;
		return Objects.equals(schema, other.schema) && name.compareToIgnoreCase(other.name) == 0;
	}

	@Override
	public String toString() {
		return "Table(" + schema + "," + name + ")";
	}

	public SchemaId getSchema() {
		return schema;
	}

	public String getName() {
		return name;
	}
}
