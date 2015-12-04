package sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class DatabaseModel {

	private final Map<TableId, TableModel> tables;

	DatabaseModel(Map<TableId, TableModel> tables) {
		Objects.requireNonNull(tables);
		this.tables = Collections.unmodifiableMap(tables);
	}

	public static DatabaseModel create() {
		return new DatabaseModel(Collections.emptyMap());
	}

	public DatabaseModel addTable(TableModel tm) {
		Objects.requireNonNull(tm);

		Map<TableId, TableModel> newTables = new HashMap<>(tables);
		TableModel old = newTables.put(tm.getId(), tm);
		if (old != null)
			throw new IllegalArgumentException("Table '" + tm.getId() + " already present");

		return new DatabaseModel(newTables);
	}

	public Collection<TableModel> getTables() {
		return new ArrayList<>(tables.values());
	}

	public TableModel getTable(TableId t) {
		return tables.get(t);
	}

	public DatabaseModel renameTables(Function<TableId, TableId> transformation) {
		Objects.requireNonNull(transformation);

		Map<TableId, TableModel> newTables = new HashMap<>();
		for (TableModel t : tables.values()) {
			TableId nid = transformation.apply(t.getId());
			TableModel nt;
			if (nid.equals(t.getId()))
				nt = t;
			else
				nt = TableModel.Transformations.rename(t, nid);

			TableModel old = newTables.put(nid, nt);
			if (old != null)
				throw new IllegalArgumentException();
		}

		return new DatabaseModel(newTables);
	}

	public DatabaseModel addColumn(ColumnModel c) {
		return modifyTable(c.getId().getTable(), (t) -> TableModel.Transformations.addColumn(t, c));
	}

	private DatabaseModel modifyTable(TableId id, Function<TableModel, TableModel> transformation) {
		Objects.requireNonNull(transformation);

		Map<TableId, TableModel> newTables = new HashMap<>();
		for (TableModel t : tables.values()) {
			TableModel nt;
			if (t.getId().equals(id)) {
				nt = transformation.apply(t);
			} else {
				nt = t;
			}

			TableModel old = newTables.put(nt.getId(), nt);
			if (old != null)
				throw new IllegalArgumentException();
		}

		return new DatabaseModel(newTables);
	}
}
