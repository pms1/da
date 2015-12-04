package da;

import java.util.Collection;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

public class ManyToOneExample2 {
	@Entity
	static public class Employee {
		@Id
		int id;
		@Embedded
		JobInfo jobInfo;
	}

	@Embeddable
	static public class JobInfo {
		String jobDescription;
		@ManyToOne
		ProgramManager pm; // Bidirectional
	}

	@Entity
	static public class ProgramManager {
		@Id
		int id;
		@OneToMany(mappedBy = "jobInfo.pm")
		Collection<Employee> manages;
	}

}
