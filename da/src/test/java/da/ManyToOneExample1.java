package da;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class ManyToOneExample1 {

	@Entity
	static public class Customer {
		@Id
		int id;
	}

	@Entity
	static public class Company {
		@Id
		int id;

		@ManyToOne(optional = false)
		@JoinColumn(name = "CUST_ID", nullable = false, updatable = false)
		Customer customer;
	}

}
