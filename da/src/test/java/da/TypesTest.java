package da;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class TypesTest {
	static enum E2 {
		FOO, BAR, FOOBAR
	}

	@Entity
	static class E1 {
		@Id
		int id;

		byte byte1;
		Byte byte2;
		byte[] byteArray;

		@Lob
		byte[] byteArrayLob;

		char char1;
		Character char2;
		char[] charArray;
		@Lob
		char[] charArrayLob;

		float float_;
		Float float2;
		double double_;
		Double double2;
		long long_;
		Long long2;
		boolean boolean_;
		Boolean boolean2;

		String string;
		@Lob
		String stringLob;

		java.math.BigDecimal bigDecimal;

		java.sql.Date sqlDate;
		java.sql.Time sqlTime;

		java.util.Date utilDate;
		@Temporal(TemporalType.DATE)
		java.util.Date utilDate1;
		@Temporal(TemporalType.TIME)
		java.util.Date utilDate2;
		@Temporal(TemporalType.TIMESTAMP)
		java.util.Date utilDate3;

		E2 enumDefault;
		@Enumerated(EnumType.ORDINAL)
		E2 enumOrdinal;
		@Enumerated(EnumType.STRING)
		E2 enumString;

		// static final Type orgJodaTimeDateTime =
		// Type.getObjectType("org/joda/time/DateTime");
		// static final Type orgJodaTimeLocalDate =
		// Type.getObjectType("org/joda/time/LocalDate");
		// static final Type orgJodaTimeLocalTime =
		// Type.getObjectType("org/joda/time/LocalTime");

		// java.time.LocalDate localDate;
	}

}
