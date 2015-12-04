package da;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

public class ManyToManyExample1Variant1 {
	@Entity
	public static class Customer {
		@Id
		@Column(name = "customer_id")
		int id;

		@ManyToMany
		public Set<PhoneNumber> phones;
	}

	@Entity
	public static class PhoneNumber {
		@Id
		@Column(name = "phonenumber_id")
		int id;

		@ManyToMany(mappedBy = "phones")
		public Set<Customer> customers;
	}

}
