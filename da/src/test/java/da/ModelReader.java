package da;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import sql.ColumnId;
import sql.ColumnModel;
import sql.DatabaseModel;
import sql.SchemaId;
import sql.TableId;
import sql.TableModel;
import sql.types.BLOBType;
import sql.types.BigIntType;
import sql.types.BooleanType;
import sql.types.CLOBType;
import sql.types.CharType;
import sql.types.DateType;
import sql.types.DecimalType;
import sql.types.DoubleType;
import sql.types.IntType;
import sql.types.SmallIntType;
import sql.types.TimeType;
import sql.types.TimestampType;
import sql.types.TinyIntType;
import sql.types.VarbinaryType;
import sql.types.VarcharType;

public abstract class ModelReader {
	final DatabaseModel read(Connection connection) throws SQLException {
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

				TableId tableId = TableId.create(SchemaId.create(s), n);
				if (!m.hasTable(tableId))
					continue;

				String col = resultSet.getString("COLUMN_NAME");

				int dataType = resultSet.getInt("DATA_TYPE");
				int columnSize = resultSet.getInt("COLUMN_SIZE");
				int decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
				int numPrecRadix = resultSet.getInt("NUM_PREC_RADIX");
				ColumnModel.Builder cm = ColumnModel.newBuilder();

				cm = cm.withId(ColumnId.create(tableId, col));

				JDBCType dataType2 = JDBCType.valueOf(dataType);

				switch (dataType2) {
				case BIGINT:
					cm = cm.withType(BigIntType.create());
					break;
				case BOOLEAN:
					cm = cm.withType(BooleanType.create());
					break;
				case CHAR:
					cm = cm.withType(CharType.create());
					break;
				case BLOB:
					cm = cm.withType(BLOBType.create());
					break;
				case CLOB:
					cm = cm.withType(CLOBType.create());
					break;
				case DATE:
					cm = cm.withType(DateType.create());
					break;
				case DECIMAL:
					cm = cm.withType(DecimalType.create());
					break;
				case DOUBLE:
					cm = cm.withType(DoubleType.create());
					break;
				case INTEGER:
					cm = cm.withType(IntType.create());
					break;
				case SMALLINT:
					cm = cm.withType(SmallIntType.create());
					break;
				case VARCHAR:
					cm = cm.withType(VarcharType.create());
					break;
				case TIME:
					cm = cm.withType(TimeType.create());
					break;
				case TIMESTAMP:
					cm = cm.withType(TimestampType.create());
					break;
				case TINYINT:
					cm = cm.withType(TinyIntType.create());
					break;
				case VARBINARY:
					cm = cm.withType(VarbinaryType.create());
					break;
				default:
					throw new Error("missing type " + dataType + " " + dataType2);
				}
				m = m.addColumn(cm.build());
			}
		}

		m = postProcess(connection, m);

		return m;
	}

	boolean ignore(String c, String s, String n) {
		return false;
	}

	DatabaseModel postProcess(Connection connection, DatabaseModel m) throws SQLException {
		return m;
	}
}
