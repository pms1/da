package da;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Intermediate2 extends Super2 {
	@Column(name = "afield")
	long a;

	@Column(name = "amethod")
	long getA() {
		return 0;
	}

	void setA(long l) {

	}

}
