package da;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(XX.class)
public class Bottom1 extends Intermediate1 {
	@Id
	long id1;

	@Id
	long id2;

	long getId1() {
		return 0;
	}

	void setId1(long l) {

	}

	@Column(name = "bfield")
	long b;

	@Column(name = "bmethod")
	long getB() {
		return 0;
	}

	void setB(long l) {

	}
}
