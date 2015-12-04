package da;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

public class ManyToMany1 {
	@Entity
	public static class Customer {
		@Id
		@Column(name = "ID")
		int id;

		@Embedded
		public PhoneNumbers phones;
	}

	public static class PhoneNumbers {
		@ManyToMany
		public Set<PhoneNumber> phones;
	}

	@Entity
	public static class PhoneNumber {
		@Id
		@Column(name = "ID")
		int id;

		@ManyToMany(mappedBy = "phones.phones")
		public Set<Customer> customers;
	}

}
