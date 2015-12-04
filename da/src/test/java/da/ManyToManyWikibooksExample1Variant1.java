package da;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

// https://en.wikibooks.org/wiki/Java_Persistence/OneToMany#Example_of_a_OneToMany_relationship_and_inverse_ManyToOne_XML
public class ManyToManyWikibooksExample1Variant1 {

	@Entity
	static public class Employee {
		@Id
		@Column(name = "ID")
		private long id;
		@ManyToMany
		private List<Project> projects;
	}

	@Entity
	static public class Project {
		@Id
		@Column(name = "ID")
		private long id;

		@Column(name = "NAME")
		private long name;
	}
}
