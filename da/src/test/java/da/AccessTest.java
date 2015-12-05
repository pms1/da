package da;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

public class AccessTest {

	@Entity
	@Access(AccessType.FIELD)
	static public class E1 {
		@Id
		@Column(name = "ID_METHOD")
		public int getId() {
			return 0;
		}

		public void setId(int id) {

		}

		@Id
		@Column(name = "ID_FIELD")
		int id;
	}

	@MappedSuperclass
	static public class E2 {
		@Id
		@Column(name = "ID_METHOD")
		public int getId() {
			return 0;
		}

		public void setId(int id) {

		}

		@Id
		@Column(name = "ID_FIELD")
		int id;
	}

	@Entity
	@Access(AccessType.FIELD)
	static public class E3F extends E2 {

	}

	@Entity
	@Access(AccessType.PROPERTY)
	static public class E3M extends E2 {

	}

	@MappedSuperclass
	@Access(AccessType.PROPERTY)
	static public class E2M {
		@Id
		@Column(name = "ID_METHOD")
		public int getId() {
			return 0;
		}

		public void setId(int id) {

		}

		@Id
		@Column(name = "ID_FIELD")
		int id;
	}

	@Entity
	@Access(AccessType.FIELD)
	static public class E3MF extends E2 {

	}

	@Entity
	@Access(AccessType.PROPERTY)
	static public class E3MM extends E2 {

	}

	@MappedSuperclass
	@Access(AccessType.FIELD)
	static public class E2F {
		@Id
		@Column(name = "ID_METHOD")
		public int getId() {
			return 0;
		}

		public void setId(int id) {

		}

		@Id
		@Column(name = "ID_FIELD")
		int id;
	}

	@Entity
	@Access(AccessType.FIELD)
	static public class E3FF extends E2 {

	}

	@Entity
	@Access(AccessType.PROPERTY)
	static public class E3FM extends E2 {

	}

	@MappedSuperclass
	static public class E41 {
		@Column(name = "VALUE_METHOD")
		public int getValue() {
			return 0;
		}

		public void setValue(int value) {

		}

		@Column(name = "VALUE_FIELD")
		int value;
	}

	@Entity
	static public class E41F extends E41 {
		@Id
		int id;
	}

	@Entity
	static public class E41M extends E41 {
		@Id
		@Column(name = "ID_METHOD")
		public int getId() {
			return 0;
		}

		public void setId(int id) {

		}
	}

	@MappedSuperclass
	@Access(AccessType.FIELD)
	static public class E42 {
		@Column(name = "VALUE_METHOD")
		public int getValue() {
			return 0;
		}

		public void setValue(int value) {

		}

		@Column(name = "VALUE_FIELD")
		int value;
	}

	@Entity
	static public class E42F extends E42 {
		@Id
		int id;
	}

	// @Entity
	// static public class E42M extends E42 {
	// @Id
	// @Column(name = "ID_METHOD")
	// public int getId() {
	// return 0;
	// }
	//
	// public void setId(int id) {
	//
	// }
	// }

	@MappedSuperclass
	@Access(AccessType.PROPERTY)
	static public class E43 {
		@Column(name = "VALUE_METHOD")
		public int getValue() {
			return 0;
		}

		public void setValue(int value) {

		}

		@Column(name = "VALUE_FIELD")
		int value;
	}

	// @Entity
	// static public class E43F extends E43 {
	// @Id
	// int id;
	// }

	@Entity
	static public class E43M extends E43 {
		@Id
		@Column(name = "ID_METHOD")
		public int getId() {
			return 0;
		}

		public void setId(int id) {

		}
	}

	@Entity
	static public class E5 {
		@Id
		int id;

		@Column
		int field1;

		@Column
		@Access(AccessType.PROPERTY)
		int getMethod1() {
			return 0;
		}

		void setMethod1(int i) {

		}

	}

	@Entity
	static public class E6 {
		@Id
		int id;

		@Column(name = "F")
		int value;

		@Column(name = "M")
		@Access(AccessType.FIELD)
		int getValue() {
			return 0;
		}

		void setValue(int i) {

		}

	}
}
