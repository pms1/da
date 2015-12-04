package da;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
// @Access(AccessType.PROPERTY)
public class Entity2 {
	@Id
	public long id;

	public Embedded1 embedded;

	public String foo;

	// @Column
	public String c1;

	@Access(AccessType.PROPERTY)
	@Column(name = "c2f")
	private int c2;

	// @Column
	@Column(name = "c2p")
	public String getC2() {
		return null;
	}

	public void setC2(String c2) {

	}

	// @Column
	// @Id
	public String getId1() {
		return null;
	}

	public void setId1(String id) {

	}

	@ElementCollection
	List<Foo> foos;
}
