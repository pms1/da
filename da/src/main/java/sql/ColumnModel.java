package sql;

import java.util.Objects;

public class ColumnModel {
	private final ColumnId id;

	private ColumnModel(ColumnId id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	public ColumnId getId() {
		return id;
	}

	public static ColumnModel create(ColumnId id) {
		Objects.requireNonNull(id);
		return new ColumnModel(id);
	}

	public static final class Transformations {
		private Transformations() {

		}

		static public ColumnModel rename(ColumnModel tm, ColumnId id) {
			return new ColumnModel(id);
		}
	}
}
