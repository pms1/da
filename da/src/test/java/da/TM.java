package da;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

import javax.persistence.Persistence;

import sql.ColumnId;
import sql.ColumnModel;
import sql.DatabaseModel;
import sql.SchemaId;
import sql.TableId;
import sql.TableModel;

public class TM {

	static class Holder {
		static final DatabaseModel dm;

		static {
			Properties properties = new Properties();
			properties.put("javax.persistence.jdbc.url",
					"jdbc:h2:mem:" + "test" + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
			// properties.put("javax.persistence.jdbc.user", "Mirko");
			// properties.put("javax.persistence.jdbc.password", "");
			StringWriter writer = new StringWriter();
			properties.put("javax.persistence.schema-generation.scripts.create-target", writer);
			properties.put("javax.persistence.schema-generation.database.action", "none");
			properties.put("javax.persistence.schema-generation.scripts.action", "create");
			properties.put("javax.persistence.schema-generation.create-database-schemas", "true");

			try {
				writer.append("create schema t2schema" + System.lineSeparator());

				Persistence.generateSchema("MyPU", properties);

				Connection connection = DriverManager
						.getConnection("jdbc:h2:mem:" + "test" + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

				System.err.println("---\n" + writer + "---\n");
				for (String s : writer.toString().split("\r?\n"))
					connection.createStatement().execute(s);

				DatabaseMetaData metaData = connection.getMetaData();

				DatabaseModel m = DatabaseModel.create();

				ResultSet resultSet = metaData.getTables("TEST", null, null, null);
				while (resultSet.next()) {
					String c = resultSet.getString("TABLE_CAT");
					String s = resultSet.getString("TABLE_SCHEMA");
					String n = resultSet.getString("TABLE_NAME");

					if (s.equals("INFORMATION_SCHEMA"))
						continue;

					m = m.addTable(TableModel.create(TableId.create(SchemaId.create(s), n)));
				}

				resultSet = metaData.getColumns("TEST", null, null, null);
				while (resultSet.next()) {
					String c = resultSet.getString("TABLE_CAT");
					String s = resultSet.getString("TABLE_SCHEMA");
					String n = resultSet.getString("TABLE_NAME");

					if (s.equals("INFORMATION_SCHEMA"))
						continue;

					String col = resultSet.getString("COLUMN_NAME");

					m = m.addColumn(ColumnModel.create(ColumnId.create(TableId.create(SchemaId.create(s), n), col)));
				}

				dm = m;
			} catch (Throwable t) {
				t.printStackTrace();
				throw new RuntimeException(t);
			}
		}
	}

	public static void main(String[] args) {
		Object x = Holder.dm;
	}
}
