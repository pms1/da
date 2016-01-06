package da;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.github.da.AnalysisConfiguration;
import com.github.da.AnalysisResult;
import com.github.da.ClasspathElementScannerConfig;
import com.github.da.DataModelCreatorConfig;
import com.github.da.TMain;
import com.github.da.jpa.HibernateH2TypeMapper;
import com.github.da.jpa.HibernateTypeMapper2;

import sql.ColumnId;
import sql.ColumnModel;
import sql.DatabaseModel;
import sql.SchemaId;
import sql.TableId;
import sql.TableModel;

public class CompareH2 {
	public static void main(String[] args) throws URISyntaxException, IOException {
		try {
			DatabaseModel hib = MyPUH2.Holder.dm;

			Class<?> c = Bottom1.class;
			Path p = findDir(c);
			AnalysisConfiguration config = new AnalysisConfiguration();
			config = config.withAnalysis(ClasspathElementScannerConfig.newBuilder() //
					.withPath(p) //
					.build());
			DataModelCreatorConfig dbmodelGen = DataModelCreatorConfig.newBuilder()
					.withTypeMapper(HibernateH2TypeMapper.class)//
					.withTypeMapper(HibernateTypeMapper2.class)//
					.build();
			config = config.withAnalysis(dbmodelGen);
			AnalysisResult ar = TMain.run(config);
			DatabaseModel dm = ar.get(DatabaseModel.class);

			Comparator c1 = new Comparator("hib", i -> {
				if (i.getSchema().equals(SchemaId.create("PUBLIC")))
					return TableId.create(SchemaId.anonymous(), i.getName());
				else
					return i;
			} , "dm", Function.identity());
			c1.compare(hib, dm);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(0);
		}

	}

	static class XX<E> {
		void xxx(E e) {

		}
	}

	static class Comparator {
		final String n1;
		final String n2;
		final Function<TableId, TableId> tf1;
		final Function<TableId, TableId> tf2;

		Comparator(String n1, Function<TableId, TableId> tf1, String n2, Function<TableId, TableId> tf2) {
			this.n1 = n1;
			this.n2 = n2;
			this.tf1 = tf1;
			this.tf2 = tf2;
		}

		public void compare(DatabaseModel dm1, DatabaseModel dm2) {
			dm1 = dm1.renameTables(tf1);
			dm2 = dm2.renameTables(tf2);
			Set<TableId> tables = new HashSet<>();
			dm1.getTables().stream().map(p -> p.getId()).forEach(tables::add);
			dm2.getTables().stream().map(p -> p.getId()).forEach(tables::add);

			for (TableId t : tables) {
				TableModel tm1 = dm1.getTable(t);
				TableModel tm2 = dm2.getTable(t);
				compare(tm1, tm2);
			}

		}

		public void compare(TableModel tm1, TableModel tm2) {
			if (tm1 == null && tm2 != null) {
				System.out.println(tm2.getId() + "\n\tNot in " + n1);
				return;
			}
			if (tm1 != null && tm2 == null) {
				System.out.println(tm1.getId() + "\n\tNot in " + n2);
				return;
			}

			Set<ColumnId> columns = new HashSet<>();
			tm1.getColumns().stream().map(p -> p.getId()).forEach(columns::add);
			tm2.getColumns().stream().map(p -> p.getId()).forEach(columns::add);

			for (ColumnId t : columns) {
				ColumnModel cm1 = tm1.getColumn(t);
				ColumnModel cm2 = tm2.getColumn(t);
				compare(cm1, cm2);
			}

		}

		public void compare(ColumnModel cm1, ColumnModel cm2) {
			if (cm1 == null && cm2 != null) {
				System.out.println(cm2.getId() + "\n\tNot in " + n1);
				return;
			}
			if (cm1 != null && cm2 == null) {
				System.out.println(cm1.getId() + "\n\tNot in " + n2);
				return;
			}
			if (cm1.getType() == null)
				System.out.println(cm1.getId() + "\n\tNo type");
			if (cm2.getType() == null)
				System.out.println(cm2.getId() + "\n\tNo type");
			if (!Objects.equals(cm1.getType(), cm2.getType())) {
				System.out.println(
						cm1.getId() + "\n\tTypes: " + n1 + "=" + cm1.getType() + " " + n2 + "=" + cm2.getType());
			}
		}
	}

	@Embeddable
	public static class E {
		public long field;

		public long getMethod() {
			return 0;
		}

		public void setMethod(long id) {

		}
	}

	@Entity
	public static class C1 {
		@Id
		public long id;

		@ElementCollection
		List<E> elements;
	}

	@Entity
	public static class C2 {
		@Id
		public long getId() {
			return 0;
		}

		public void setId(long id) {
		}

		@ElementCollection
		public List<E> getElements() {
			return null;
		}

		public void setElements(List<E> es) {

		}
	}

	@Entity
	@Table(name = "t1table")
	public static class T1 {
		@Id
		public long id;

		@ElementCollection
		List<E> elements;
	}

	@Entity
	@Table(schema = "t2schema")
	public static class T2 {
		@Id
		public long id;

		@ElementCollection
		List<E> elements;
	}

	@Entity
	public static class T3 {
		@Id
		public long id;

		@ElementCollection
		@CollectionTable(name = "t3elements")
		List<E> elements;
	}

	@Entity
	public static class T4 {
		@Id
		public long id;

		@ElementCollection
		@OrderColumn(name = "order_column")
		List<E> elements;
	}

	@Entity
	public static class T5 {
		@Id
		public long id;

		@ElementCollection
		@OrderColumn
		List<E> elements;
	}

	@Entity
	public static class T6 {
		@Id
		public long id;

		@ElementCollection
		@AttributeOverride(name = "field", column = @Column(name = "overrideField") )
		List<E> elements;
	}

	@Entity
	public static class T7 {
		@Id
		public long id;

		@ElementCollection
		@CollectionTable(joinColumns = @JoinColumn(name = "OVERRIDE_ID") )
		List<E> elements;
	}

	@Entity
	public static class T72 {
		@Id
		public long id;

		@ElementCollection
		@CollectionTable(joinColumns = @JoinColumn(name = "OVERRIDE_ID", referencedColumnName = "id") )
		List<E> elements;
	}

	// @Entity
	// public static class T73 {
	// @Id
	// public long id;
	//
	// @ElementCollection
	// @CollectionTable(joinColumns = @JoinColumn(name = "OVERRIDE_ID",
	// referencedColumnName = "notId") )
	// List<E> elements;
	// }

	@Entity
	public static class T71 {
		@Id
		@Column(name = "overrideId")
		public long id;

		@ElementCollection
		List<E> elements;
	}

	public static class T8Key implements Serializable {
		public long id1;

		public long id2;
	}

	@Entity
	@IdClass(T8Key.class)
	public static class T8 {
		@Id
		public long id1;
		@Id
		public long id2;

		@ElementCollection
		List<E> elements;
	}

	@Entity
	@IdClass(T8Key.class)
	public static class T8_1 {
		@Id
		public long id1;
		@Id
		public long id2;

		@ElementCollection
		@CollectionTable(joinColumns = { @JoinColumn(referencedColumnName = "id1", name = "OVERRIDE_ID1"),
				@JoinColumn(referencedColumnName = "id2", name = "OVERRIDE_ID2") })
		List<E> elements;
	}

	@Entity
	@IdClass(T8Key.class)
	public static class T8_2 {
		@Id
		@Column(name = "id3")
		public long id1;
		@Id
		public long id2;

		@ElementCollection
		@CollectionTable(joinColumns = { @JoinColumn(referencedColumnName = "id3", name = "OVERRIDE_ID1"),
				@JoinColumn(referencedColumnName = "id2", name = "OVERRIDE_ID2") })
		List<E> elements;
	}

	abstract class XXX<X> implements List<X> {

	}

	public static Path findDir(Class<?> c) throws URISyntaxException {
		ClassLoader cl = c.getClassLoader();

		URLClassLoader cl1 = (URLClassLoader) cl;

		URL[] urls = cl1.getURLs();

		for (URL u : urls) {
			switch (u.getProtocol()) {
			case "file":
				String f = c.getCanonicalName().replace('.', '/') + ".class";
				Path p = Paths.get(u.toURI());
				if (Files.isDirectory(p)) {
					if (Files.exists(p.resolve(f))) {
						return p;
					}
				}
				System.err.println(p);
				break;
			default:
				throw new Error();
			}
		}
		throw new Error();
	}

	static abstract class Super1<A> {
		abstract void setBase(A a);

		abstract A getBase();
	}

	@Table
	static class Extends1 extends Super1<Integer> {
		@Id
		long getId() {
			return 0;
		}

		void setId(long id) {

		}

		void setBase(Integer a) {

		}

		Integer getBase() {
			return null;
		}
	}
}
