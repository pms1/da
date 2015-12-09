package da;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.persistence.Persistence;

import sql.DatabaseModel;
import sql.SchemaIdVisitor;
import sql.TableModel;

public class MyPUDB2 {

	static class Holder {
		static final DatabaseModel dm;

		static {
			String url = "jdbc:db2://localhost:50000/DBMGEN";
			String user = "db2admin";
			String pass = "db2-Admin";
			Properties properties = new Properties();
			properties.put("javax.persistence.jdbc.url", url);
			properties.put("javax.persistence.jdbc.user", user);
			properties.put("javax.persistence.jdbc.password", pass);
			StringWriter writer = new StringWriter();
			properties.put("javax.persistence.schema-generation.scripts.create-target", writer);
			properties.put("javax.persistence.schema-generation.database.action", "none");
			properties.put("javax.persistence.schema-generation.scripts.action", "create");
			properties.put("javax.persistence.schema-generation.create-database-schemas", "true");

			try {
				try (Connection connection = DriverManager.getConnection(url, user, pass)) {

					DatabaseModel read = new DB2ModelReader().read(connection);
					read.getTables().stream().map(TableModel::getId).forEach(id -> {
						String sql = id.getSchema().accept(new SchemaIdVisitor<String>() {
							public String visitName(String s) {
								return s + ".";
							}

							@Override
							public String visitAnonymous() {
								return "";
							}
						});

						// String t;
						try {
							String s = "drop table " + sql + id.getName();
							System.err.print(s);
							connection.createStatement().execute(s);
							System.err.println();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					});

					Persistence.generateSchema("MyPU", properties);

					System.err.println("---\n" + writer + "---\n");
					for (String s : writer.toString().split("\r?\n")) {
						System.err.print(s);
						connection.createStatement().execute(s);
						System.err.println();
					}

					DatabaseModel m = new DB2ModelReader().read(connection);

					dm = m;
				}
			} catch (Throwable t) {
				t.printStackTrace();
				throw new RuntimeException(t);
			}
		}
	}

	public static void main(String[] args) {
		Object x = Holder.dm;
		System.exit(0);
	}
}
