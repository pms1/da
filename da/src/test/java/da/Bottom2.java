package da;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Bottom2 extends Intermediate2 {
	@Column(name = "bfield")
	long b;

	@Column(name = "bmethod")
	long getB() {
		return 0;
	}

	void setB(long l) {

	}
}
