package da;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sql.DatabaseModel;
import sql.SchemaId;
import sql.TableId;

public class DB2ModelReader extends ModelReader {

	@Override
	boolean ignore(String c, String s, String n) {
		return s.equals("SYSTOOLS");
	}

	@Override
	DatabaseModel postProcess(Connection connection, DatabaseModel m) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			if (!statement.execute(
					"select rtrim(tabschema),tabname,rtrim(base_tabschema),base_tabname from syscat.tables where ownertype != 'S' and type = 'A'"))
				throw new Error();

			try (ResultSet resultSet = statement.getResultSet()) {
				while (resultSet.next()) {
					String tabschema = resultSet.getString(1);
					String tabname = resultSet.getString(2);
					String base_tabschema = resultSet.getString(3);
					String base_tabname = resultSet.getString(4);

					TableId id = TableId.create(SchemaId.create(base_tabschema), base_tabname);
					TableId alias = TableId.create(SchemaId.create(tabschema), tabname);

					m = m.addAlias(id, alias);
				}
			}

		}

		return m;
	}
}
