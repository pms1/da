package da;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

public class CollectionTableTest {
	static enum WaypointType {

	}

	static class Waypoint {
		@Id
		int id;
		@ElementCollection(targetClass = WaypointType.class, fetch = FetchType.EAGER)
		@CollectionTable(name = "WAYPOINT_TYPES", schema = "PPM", joinColumns = { @JoinColumn(name = "WAYPOINT_OID") })
		@Column(name = "XXX")
		@Enumerated(EnumType.STRING)
		private Set<WaypointType> waypointTypes = new HashSet<>();
	}
}
