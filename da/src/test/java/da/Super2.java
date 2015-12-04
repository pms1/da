package da;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Super2 {
	@Id
	public long id;

}
