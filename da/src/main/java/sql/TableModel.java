package sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TableModel {
	private final TableId id;
	private final Map<ColumnId, ColumnModel> columns;

	private TableModel(TableId id, Map<ColumnId, ColumnModel> columns) {
		Objects.requireNonNull(id);
		this.id = id;
		this.columns = columns;
	}

	public TableId getId() {
		return id;
	}

	public static TableModel create(TableId id) {
		Objects.requireNonNull(id);
		return new TableModel(id, Collections.emptyMap());
	}

	public static final class Transformations {
		private Transformations() {

		}

		static public TableModel rename(TableModel tm, TableId id) {
			Map<ColumnId, ColumnModel> ncolumns = new LinkedHashMap<>(tm.columns.size());
			for (ColumnModel c : tm.columns.values()) {
				ColumnId nid = ColumnId.create(id, c.getId().getName());
				ncolumns.put(nid, ColumnModel.Transformations.rename(c, nid));
			}
			return new TableModel(id, Collections.unmodifiableMap(ncolumns));
		}

		static public TableModel addColumn(TableModel tm, ColumnModel m) {
			Objects.requireNonNull(tm);
			Objects.requireNonNull(m);
			if (!m.getId().getTable().equals(tm.getId()))
				throw new IllegalArgumentException();
			if (tm.columns.containsKey(m.getId()))
				throw new IllegalArgumentException("Table '" + tm.getId() + "' already contains '" + m.getId() + "'");
			Map<ColumnId, ColumnModel> ncolumns = new LinkedHashMap<>(tm.columns);
			ncolumns.put(m.getId(), m);
			return new TableModel(tm.getId(), Collections.unmodifiableMap(ncolumns));
		}
	}

	public List<ColumnModel> getColumns() {
		return new ArrayList<>(columns.values());
	}

	public Map<ColumnId, ColumnModel> getColumnsAsMap() {
		return new HashMap<>(columns);
	}

	public ColumnModel getColumn(ColumnId t) {
		return columns.get(t);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("TableModel(" + id + ",");
		b.append(columns.keySet().stream().map(ColumnId::getName).collect(Collectors.joining(",")));
		return b.toString();
	}
}
