package da;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

public class OneToOneTest {
	@Entity
	static public class E1 {
		@Id
		long id;
	}

	@Entity
	static public class E2 {
		@Id
		long id;

		@OneToOne
		E1 e1;
	}

	@Entity
	static public class E21 {
		@Id
		long id;

		@OneToOne
		@JoinColumn(name = "overrideE1")
		E1 e1;

	}

	@Entity
	static public class Employee {
		@Id
		int id;
		@Embedded
		LocationDetails location;
	}

	@Embeddable
	static public class LocationDetails {
		int officeNumber;
		@OneToOne
		ParkingSpot parkingSpot;
	}

	@Entity
	static public class ParkingSpot {
		@Id
		int id;
		String garage;
		@OneToOne(mappedBy = "location.parkingSpot")
		Employee assignedTo;
	}

}
