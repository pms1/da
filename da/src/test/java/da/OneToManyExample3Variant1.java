package da;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

public class OneToManyExample3Variant1 {
	@Entity
	static class Order {
		@Id
		int id;
	}

	@Entity
	static class Customer {
		@Id
		int id;
		@OneToMany(orphanRemoval = true)
		@JoinColumn(name = "CUST_ID") // join column is in table for Order
		Set<Order> orders;
	}
}
