package da;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import sql.ColumnId;
import sql.ColumnModel;
import sql.DatabaseModel;
import sql.SchemaId;
import sql.TableId;
import sql.TableModel;

public class ModelReader {
	DatabaseModel read(Connection connection) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();

		DatabaseModel m = DatabaseModel.create();

		try (ResultSet resultSet = metaData.getTables(null, null, null, null)) {
			while (resultSet.next()) {
				String c = resultSet.getString("TABLE_CAT");
				String s = resultSet.getString("TABLE_SCHEM");
				String n = resultSet.getString("TABLE_NAME");

				if (!Objects.equals(resultSet.getString("TABLE_TYPE"), "TABLE"))
					continue;

				if (ignore(c, s, n))
					continue;

				m = m.addTable(TableModel.create(TableId.create(SchemaId.create(s), n)));
			}
		}

		try (ResultSet resultSet = metaData.getColumns(null, null, null, null)) {
			while (resultSet.next()) {
				String c = resultSet.getString("TABLE_CAT");
				String s = resultSet.getString("TABLE_SCHEM");
				String n = resultSet.getString("TABLE_NAME");

				String col = resultSet.getString("COLUMN_NAME");

				m = m.addColumn(ColumnModel.create(ColumnId.create(TableId.create(SchemaId.create(s), n), col)));
			}
		}

		m = postProcess(connection, m);

		return m;
	}

	private boolean ignore(String c, String s, String n) {
		return s.equals("SYSTOOLS");
	}

	private DatabaseModel postProcess(Connection connection, DatabaseModel m) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			if (!statement.execute(
					"select rtrim(tabschema),tabname,rtrim(base_tabschema),base_tabname from syscat.tables where ownertype != 'S' and type = 'A'"))
				throw new Error();

			Multimap<TableId, TableId> aliases = HashMultimap.create();
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
