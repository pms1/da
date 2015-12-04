package da;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Intermediate1 extends Super1 {
	@Column(name = "afield")
	long a;

	@Column(name = "amethod")
	long getA() {
		return 0;
	}

	void setA(long l) {

	}

	@AttributeOverride(name = "date", column = @Column(name = "oefield") )
	Embedded1 e1;

	@AttributeOverride(name = "date", column = @Column(name = "oemethod") )
	Embedded1 getE1() {
		return null;
	}

	void setE1(Embedded1 e1) {

	}
}
