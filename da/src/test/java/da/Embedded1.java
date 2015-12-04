package da;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Embedded1 {
	// @Access(AccessType.PROPERTY)
	@Column(name = "efield")
	private Date date;

	public void setDate(String date) {

	}

	@Column(name = "emethod")
	public String getDate() {
		return null;
	}

	@Column(length = 42)
	private String str;
}
