package sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class DatabaseModel {

	private final Map<TableId, TableModel> tables;
	private final Multimap<TableId, TableId> aliases;

	DatabaseModel(Map<TableId, TableModel> tables, Multimap<TableId, TableId> aliases) {
		Objects.requireNonNull(tables);
		Objects.requireNonNull(aliases);
		this.tables = Collections.unmodifiableMap(tables);
		this.aliases = Multimaps.unmodifiableMultimap(aliases);

	}

	public static DatabaseModel create() {
		return new DatabaseModel(Collections.emptyMap(), HashMultimap.create());
	}

	class BuilderImpl {
		Map<TableId, TableModel> tables = DatabaseModel.this.tables;
		Multimap<TableId, TableId> aliases = DatabaseModel.this.aliases;

		DatabaseModel build() {
			return new DatabaseModel(tables, aliases);
		}
	}

	public DatabaseModel addTable(TableModel tm) {
		Objects.requireNonNull(tm);

		BuilderImpl builder = new BuilderImpl();

		builder.tables = new HashMap<>(builder.tables);
		TableModel old = builder.tables.put(tm.getId(), tm);
		if (old != null)
			throw new IllegalArgumentException("Table '" + tm.getId() + " already present");

		return builder.build();
	}

	public DatabaseModel removeTable(TableId t) {
		Objects.requireNonNull(t);

		BuilderImpl builder = new BuilderImpl();

		builder.tables = new HashMap<>(builder.tables);
		TableModel old = builder.tables.remove(t);
		if (old == null)
			throw new IllegalArgumentException("Table '" + t + " not present");

		return builder.build();
	}

	public Collection<TableModel> getTables() {
		return new ArrayList<>(tables.values());
	}

	public TableModel getTable(TableId t) {
		return tables.get(t);
	}

	public DatabaseModel renameTables(Function<TableId, TableId> transformation) {
		Objects.requireNonNull(transformation);

		BuilderImpl builder = new BuilderImpl();
		builder.tables = new HashMap<>();
		for (TableModel t : tables.values()) {
			TableId nid = transformation.apply(t.getId());
			TableModel nt;
			if (nid.equals(t.getId()))
				nt = t;
			else
				nt = TableModel.Transformations.rename(t, nid);

			TableModel old = builder.tables.put(nid, nt);
			if (old != null)
				throw new IllegalArgumentException();
		}

		return builder.build();
	}

	public DatabaseModel addColumn(ColumnModel c) {
		return modifyTable(c.getId().getTable(), (t) -> TableModel.Transformations.addColumn(t, c));
	}

	private DatabaseModel modifyTable(TableId id, Function<TableModel, TableModel> transformation) {
		Objects.requireNonNull(transformation);

		BuilderImpl builder = new BuilderImpl();
		builder.tables = new HashMap<>();
		for (TableModel t : tables.values()) {
			TableModel nt;
			if (t.getId().equals(id)) {
				nt = transformation.apply(t);
			} else {
				nt = t;
			}

			TableModel old = builder.tables.put(nt.getId(), nt);
			if (old != null)
				throw new IllegalArgumentException();
		}
		return builder.build();
	}

	public DatabaseModel addAlias(TableId id, TableId alias) {
		BuilderImpl builder = new BuilderImpl();
		if (builder.aliases.containsEntry(id, alias))
			throw new IllegalArgumentException();
		builder.aliases = HashMultimap.create(builder.aliases);
		builder.aliases.put(id, alias);

		return builder.build();
	}

	public Multimap<TableId, TableId> getAliases() {
		return HashMultimap.create(aliases);
	}
}
