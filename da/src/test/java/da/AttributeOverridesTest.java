package da;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

public class AttributeOverridesTest {
	@Embeddable
	public static class E {
		public long field;

		public long getMethod() {
			return 0;
		}

		public void setMethod(long id) {

		}
	}

	@Entity
	public static class Over1 {
		@Id
		public long id;

		@Embedded
		@AttributeOverride(name = "field", column = @Column(name = "overrideField") )
		public E e1;
	}

	@Entity
	public static class Over2 {
		@Id
		public long id;

		@Embedded
		@AttributeOverrides(@AttributeOverride(name = "field", column = @Column(name = "overrideField") ))
		public E e1;
	}

	@Embeddable
	public static class E2 {

		public long field1;

		public long getMethod1() {
			return 0;
		}

		public void setMethod1(long id) {

		}

		public E field2;

		public E getMethod2() {
			return null;
		}

		public void setMethod2(E id) {

		}
	}

	@Embeddable
	public static class E3 {

		public long field1;

		public long getMethod1() {
			return 0;
		}

		public void setMethod1(long id) {

		}

		@AttributeOverrides(@AttributeOverride(name = "field", column = @Column(name = "overrideField") ))
		public E field2;

		public E getMethod2() {
			return null;
		}

		public void setMethod2(E id) {

		}
	}

	@Entity
	public static class Over3 {
		@Id
		public long id;

		@Embedded
		@AttributeOverride(name = "field2.field", column = @Column(name = "overrideField") )
		public E2 e1;
	}

	@Entity
	public static class Over4 {
		@Id
		public long id;

		@Embedded
		@AttributeOverrides({ @AttributeOverride(name = "field1", column = @Column(name = "overrideField1") ),
				@AttributeOverride(name = "field2.field", column = @Column(name = "overrideField2") ) })
		public E2 e1;
	}

	@Entity
	public static class Over5 {
		@Id
		public long id;

		@Embedded
		@AttributeOverride(name = "field2.field", column = @Column(name = "overrideField2") )
		public E3 e1;
	}
}
