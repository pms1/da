package da;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

// https://en.wikibooks.org/wiki/Java_Persistence/OneToMany#Example_of_a_OneToMany_relationship_and_inverse_ManyToOne_XML
public class OneToManyWikibooksExample1 {

	@Entity
	static public class Employee {
		@Id
		@Column(name = "EMP_ID")
		private long id;

		@OneToMany(mappedBy = "owner")
		private List<Phone> phones;

	}

	@Entity
	static public class Phone {
		@Id
		private long id;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "OWNER_ID")
		private Employee owner;

	}

}
