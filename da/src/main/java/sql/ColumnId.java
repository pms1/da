package sql;

import java.util.Objects;

public class ColumnId {
	private final TableId table;
	private final String name;

	private ColumnId(TableId table, String name) {
		Objects.requireNonNull(table);
		this.table = table;
		Objects.requireNonNull(name);
		if (name.isEmpty())
			throw new IllegalArgumentException();
		this.name = name;
	}

	public static ColumnId create(TableId table, String name) {
		return new ColumnId(table, name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(table, name.toLowerCase());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnId other = (ColumnId) obj;
		return Objects.equals(table, other.table) && name.compareToIgnoreCase(other.name) == 0;
	}

	@Override
	public String toString() {
		return "Column(" + table + "," + name + ")";
	}

	public TableId getTable() {
		return table;
	}

	public String getName() {
		return name;
	}
}
