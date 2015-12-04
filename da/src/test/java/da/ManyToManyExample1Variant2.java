package da;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

public class ManyToManyExample1Variant2 {
	@Entity
	public static class Customer {
		@Id
		int id;

		@ManyToMany
		public Set<PhoneNumber> phones;
	}

	@Entity
	public static class PhoneNumber {
		@Id
		int id;

		@ManyToMany(mappedBy = "phones")
		public Set<Customer> customers;
	}

}
