package da;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

public class ManyToManyExample1 {
	@Entity
	public static class Customer {
		@Id
		@Column(name = "ID")
		int id;

		@ManyToMany
		@JoinTable(name = "CUST_PHONES")
		public Set<PhoneNumber> phones;
	}

	@Entity
	public static class PhoneNumber {
		@Id
		@Column(name = "ID")
		int id;

		@ManyToMany(mappedBy = "phones")
		public Set<Customer> customers;
	}

}
