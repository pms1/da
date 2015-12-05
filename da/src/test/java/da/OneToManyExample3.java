package da;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;

public class OneToManyExample3 {
	@Entity
	static class Order {
		@Id
		int id;
	}

	static class CustomerId implements Serializable {
		public int id1;
		public int id2;
	}

	@Entity
	@IdClass(CustomerId.class)
	static class Customer {
		@Id
		int id1;
		@Id
		int id2;
		@OneToMany(orphanRemoval = true)
		@JoinColumns({ @JoinColumn(name = "OVERRIDE_ID1", referencedColumnName = "ID1"),
				@JoinColumn(name = "OVERRIDE_ID2", referencedColumnName = "ID2") })
		Set<Order> orders;
	}
}
