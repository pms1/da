package sql;

import java.util.Objects;

public abstract class SchemaId {

	public static SchemaId create(String name) {
		return new SchemaIdName(name);
	}

	public static SchemaId anonymous() {
		return anonymous;
	}

	static class SchemaIdName extends SchemaId {
		private final String name;

		SchemaIdName(String name) {
			Objects.requireNonNull(name);
			if (name.isEmpty())
				throw new IllegalArgumentException();
			this.name = name;
		}

		@Override
		public int hashCode() {
			return Objects.hash(name.toLowerCase());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SchemaIdName other = (SchemaIdName) obj;
			return name.compareToIgnoreCase(other.name) == 0;
		}

		@Override
		public String toString() {
			return "Schema(" + name + ")";
		}

		@Override
		public <T> T accept(SchemaIdVisitor<T> visitor) {
			return visitor.visitName(name);
		}

	}

	private static final SchemaId anonymous = new SchemaId() {
		public String toString() {
			return "Schema()";
		}

		@Override
		public <T> T accept(SchemaIdVisitor<T> visitor) {
			return visitor.visitAnonymous();
		};

	};

	public abstract <T> T accept(SchemaIdVisitor<T> visitor);
}
