package da;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

public class ManyToManyExample3 {
	@Entity
	static class Customer {
		@Id
		@Column(name = "ID")
		int id;

		@ManyToMany
		@JoinTable(name = "CUST_PHONE", joinColumns = @JoinColumn(name = "CUST_ID", referencedColumnName = "ID") , inverseJoinColumns = @JoinColumn(name = "PHONE_ID", referencedColumnName = "ID") )
		Set<PhoneNumber> phones;
	}

	@Entity
	static class PhoneNumber {
		@Id
		@Column(name = "ID")
		int id;

		@ManyToMany(mappedBy = "phones")
		Set<Customer> customers;
	}

}
