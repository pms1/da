package da;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

// Example from @OneToMany
public class OneToManyExample1 {

	@Entity
	static public class Order {
		@Id
		public int id;

		// Customer customer;
	}

	@Entity
	static public class Customer {
		@Id
		public int id;

		@OneToMany(cascade = CascadeType.ALL /* , mappedBy = "customer" */)
		Set<Order> orders;
	}

}
