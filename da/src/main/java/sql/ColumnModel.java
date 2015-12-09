package sql;

import java.util.Objects;

import sql.types.SqlType;

public class ColumnModel {
	private final ColumnId id;
	private final SqlType type;

	private ColumnModel(ColumnId id, SqlType type) {
		Objects.requireNonNull(id);
		this.id = id;
		this.type = type;
	}

	public ColumnId getId() {
		return id;
	}

	public SqlType getType() {
		return type;
	}

	public static ColumnModel create(ColumnId id) {
		Objects.requireNonNull(id);
		return new ColumnModel(id, null);
	}

	public static final class Transformations {
		private Transformations() {

		}

		static public ColumnModel rename(ColumnModel tm, ColumnId id) {
			BuilderImpl builderImpl = new BuilderImpl(tm);
			builderImpl.id = id;
			return builderImpl.build();
		}
	}

	public interface Builder {
		ColumnModel build();

		Builder withId(ColumnId id);

		Builder withType(SqlType type);
	}

	public static Builder newBuilder() {
		return new BuilderImpl();
	}

	private static class BuilderImpl implements Builder {
		ColumnId id;
		SqlType type;

		BuilderImpl() {

		}

		BuilderImpl(ColumnModel cm) {
			id = cm.id;
			type = cm.type;
		}

		@Override
		public Builder withId(ColumnId id) {
			Objects.requireNonNull(id);
			this.id = id;
			return this;
		}

		@Override
		public Builder withType(SqlType type) {
			this.type = type;
			return this;
		}

		@Override
		public ColumnModel build() {
			return new ColumnModel(id, type);
		}
	}

	@Override
	public String toString() {
		return "ColumnModel(" + id + "," + type + ")";
	}
}
