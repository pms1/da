package da;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

public class MappedSuperclassTest {

	@MappedSuperclass
	static class Super1 {
		@Column(name = "super1f")
		Integer super1;

		@Column(name = "super1m")
		Integer getSuper1() {
			return null;
		}

		void setSuper1(Integer super1) {
		}

	}

	@MappedSuperclass
	static class Super2 {
		@Column(name = "super1f")
		Integer super1;

		@Column(name = "super1m")
		Integer getSuper1() {
			return null;
		}

		void setSuper1(Integer super1) {
		}

	}

	@Entity
	static class C1F extends Super1 {
		@Id
		int id;
	}

	@Entity
	static class C1M extends Super1 {
		@Id
		int getId() {
			return 0;
		}

		void setId(int id) {

		}
	}

	@Entity
	static class C2F extends Super2 {
		@Id
		int id;
	}

	@Entity
	static class C2M extends Super2 {
		@Id
		int getId() {
			return 0;
		}

		void setId(int id) {

		}
	}

	static class Inter11 extends Super1 {
		@Column(name = "inter11f")
		Integer inter11;

		@Column(name = "inter11m")
		Integer getInter11() {
			return null;
		}

		void setInter11(Integer inter11) {
		}

	}

	@MappedSuperclass
	static class Inter12 extends Super1 {
		@Column(name = "inter12f")
		Integer inter12;

		@Column(name = "inter12m")
		Integer getInter12() {
			return null;
		}

		void setInter12(Integer inter12) {
		}
	}

	static class Inter21 extends Super1 {

	}

	@MappedSuperclass
	static class Inter22 extends Super1 {

	}

	@Entity
	static class C11F extends Inter11 {
		@Id
		int id;
	}

	@Entity
	static class C11M extends Inter11 {
		@Id
		int getId() {
			return 0;
		}

		void setId(int id) {

		}
	}

	@Entity
	static class C22F extends Inter22 {
		@Id
		int id;
	}

	@Entity
	static class C22M extends Inter22 {
		@Id
		int getId() {
			return 0;
		}

		void setId(int id) {

		}
	}

	@Entity
	static class C21F extends Inter21 {
		@Id
		int id;
	}

	@Entity
	static class C21M extends Inter21 {
		@Id
		int getId() {
			return 0;
		}

		void setId(int id) {

		}
	}

	@Entity
	static class C12F extends Inter12 {
		@Id
		int id;
	}

	@Entity
	static class C12M extends Inter12 {
		@Id
		int getId() {
			return 0;
		}

		void setId(int id) {

		}
	}
}
