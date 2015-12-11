package da.jpa.gen;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.github.da.jpa.model.persistence_2_0.Persistence;
import com.github.da.jpa.model.persistence_2_0.Persistence.PersistenceUnit;

public class M1 {
	public static void main(String[] args) throws Exception {

		JAXBContext jaxbContext = JAXBContext.newInstance(Persistence.class.getPackage().getName());

		System.err.println(jaxbContext.getClass());

		Method m = jaxbContext.getClass().getMethod("getBuildId");
		System.err.println(m.invoke(jaxbContext));

		// if (jaxbContext instanceof
		// com.sun.xml.bind.v2.runtime.JAXBContextImpl) {
		// System.out.println(
		// "JAXB Version: " + ((com.sun.xml.bind.v2.runtime.JAXBContextImpl)
		// jaxbContext).getBuildId());
		// } else {
		// System.out.println("Unknown JAXB implementation: " +
		// jaxbContext.getClass().getName());
		// }

		Unmarshaller u = jaxbContext.createUnmarshaller();
		FileInputStream is = new FileInputStream(new File(
				"w:/priv/workspaces/sitescan/workspace/da.git/da/src/test/resources/META-INF/persistence.xml"));
		Persistence c = u.unmarshal(new StreamSource(is), Persistence.class).getValue();
		System.err.println("C=" + c);
		System.err.println("C=" + c.getVersion());
		for (PersistenceUnit pu : c.getPersistenceUnit()) {
		}
	}
}
