package da;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

public class ImplicitEmbeddedTest {
	static class E1 {
		Integer e1f;

		Integer getE1m() {
			return null;
		}

		void setE1m(Integer e) {

		}
	}

	static class E2 {
		Integer e2f;

		Integer getE2m() {
			return null;
		}

		void setE2m(Integer e) {

		}
	}

	@Entity
	static class C1F {
		@Id
		int id;

		@Embedded
		E1 e1;

		// E2 e2;
	}
}
