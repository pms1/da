package com.github.da;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import sql.ColumnId;
import sql.ColumnModel;
import sql.DatabaseModel;
import sql.SchemaId;
import sql.TableId;
import sql.TableModel;

public class DataModelCreator {
	private final ClassHierarchy ch;

	public DataModelCreator(ClassHierarchy ch) {
		Objects.requireNonNull(ch);
		this.ch = ch;
	}

	static TableId createTableId(String overrideSchema, String overrideTable, String defaultTable) {
		SchemaId s;
		if (!Strings.isNullOrEmpty(overrideSchema))
			s = SchemaId.create(overrideSchema);
		else
			s = SchemaId.anonymous();
		TableId t;

		if (!Strings.isNullOrEmpty(overrideTable))
			t = TableId.create(s, overrideTable);
		else
			t = TableId.create(s, defaultTable);
		return t;
	}

	static TableId createTableId(TableAnnotation table, String name) {
		return table != null ? createTableId(table.schema, table.name, name) : createTableId(null, null, name);
	}

	static TableId createTableId(JoinTableAnnotation table, String name) {
		return table != null ? createTableId(table.schema, table.name, name) : createTableId(null, null, name);
	}

	static TableId createTableId(CollectionTableAnnotation table, String name) {
		return table != null ? createTableId(table.schema, table.name, name) : createTableId(null, null, name);
	}

	DatabaseModel dm;

	String columnName(JpaProperty p) {
		return (p.column != null && !Strings.isNullOrEmpty(p.column.name)) ? p.column.name : removeLead(p.name);
	}

	static String removeLead(String s) {
		return s.substring(s.lastIndexOf('.') + 1);
	}

	ColumnId columnId(TableId tableId, JpaProperty p) {
		return ColumnId.create(tableId, columnName(p));
	}

	void createJoinTable(JpaProperty p, String className, Collection<JpaProperty> collection) {

		ClassModel other = ch.get(p.elementType);
		if (other == null)
			throw new Error();
		JpaAnalysisResult ja = other.get(JpaAnalysisResult.class);
		if (ja == null)
			throw new Error();
		if (!ja.isEntity())
			throw new Error();

		String className2 = p.elementType.getClassName();

		TableModel em = TableModel.create(createTableId(p.joinTable, className.substring(className.lastIndexOf('.') + 1)
				+ "_" + className2.substring(className2.lastIndexOf('.') + 1)));

		List<JpaProperty> idProperties = collection.stream().filter((q) -> q.id).collect(Collectors.toList());

		em = addKeyReference(em, idProperties, p.joinTable != null ? p.joinTable.joinColumns : null,
				(x) -> p.name + "_" + x.name);

		idProperties = ja.properties.values().stream().filter((q) -> q.id).collect(Collectors.toList());

		em = addKeyReference(em, idProperties, p.joinTable != null ? p.joinTable.inverseJoinColumns : null,
				(x) -> className.substring(className.lastIndexOf('.') + 1) + "_" + x.name);

		dm = dm.addTable(em);
	}

	void createJoinTableMany(JpaProperty p, String className, Collection<JpaProperty> collection) {

		ClassModel other = ch.get(p.elementType);
		if (other == null)
			throw new Error();
		JpaAnalysisResult ja = other.get(JpaAnalysisResult.class);
		if (ja == null)
			throw new Error();
		if (!ja.isEntity())
			throw new Error();

		String className2 = p.elementType.getClassName();

		Optional<JpaProperty> reverse = ja.properties.values().stream()
				.filter((p1) -> p1.manytomany != null && Objects.equals(p1.manytomany.mappedBy, p.name)).findFirst();

		TableModel em = TableModel.create(createTableId(p.joinTable, className.substring(className.lastIndexOf('.') + 1)
				+ "_" + className2.substring(className2.lastIndexOf('.') + 1)));

		List<JpaProperty> idProperties = collection.stream().filter((q) -> q.id).collect(Collectors.toList());

		em = addKeyReference(em, idProperties, p.joinTable != null ? p.joinTable.joinColumns : null,
				(x) -> /* FIXME */ removeLead((reverse.isPresent() ? reverse.get().name : p.name)) + "_"
						+ columnName(x));

		idProperties = ja.properties.values().stream().filter((q) -> q.id).collect(Collectors.toList());

		em = addKeyReference(em, idProperties, p.joinTable != null ? p.joinTable.inverseJoinColumns : null,
				(x) -> (reverse.isPresent() ? /* FIXME */removeLead(p.name)
						: className.substring(className.lastIndexOf('.') + 1)) + "_" + columnName(x));

		dm = dm.addTable(em);
	}

	public TableModel addColumns(TableModel tableModel, String className, Collection<JpaProperty> collection) {
		for (JpaProperty p : collection) {
			switch (p.fieldType) {
			case VALUE:
				ColumnModel cm = ColumnModel.create(columnId(tableModel.getId(), p));
				tableModel = TableModel.Transformations.addColumn(tableModel, cm);
				break;
			case ONE_TO_ONE:
				ClassModel other = ch.get(p.type);
				if (other == null)
					throw new Error();
				JpaAnalysisResult ja = other.get(JpaAnalysisResult.class);
				if (ja == null)
					throw new Error();
				if (!ja.isEntity())
					throw new Error();

				if (Strings.isNullOrEmpty(p.onetoone.mappedBy)) {
					List<JpaProperty> idProperties = ja.properties.values().stream().filter((q) -> q.id)
							.collect(Collectors.toList());

					tableModel = addKeyReference(tableModel, idProperties, p.joinColumns,
							(oname) -> /* FIXME */ removeLead(p.name) + "_" + oname.name);
				}

				break;
			case MANY_TO_ONE:
				other = ch.get(p.type);
				if (other == null)
					throw new Error();
				ja = other.get(JpaAnalysisResult.class);
				if (ja == null)
					throw new Error();
				if (!ja.isEntity())
					throw new Error();

				List<JpaProperty> idProperties = ja.properties.values().stream().filter((q) -> q.id)
						.collect(Collectors.toList());

				tableModel = addKeyReference(tableModel, idProperties, p.joinColumns,
						(oname) -> /* FIXME */ removeLead(p.name) + "_" + oname.name);

				break;
			case ONE_TO_MANY:
				if (Strings.isNullOrEmpty(p.onetomany.mappedBy)) {
					createJoinTable(p, className, collection);
				}
				break;
			case MANY_TO_MANY:
				if (Strings.isNullOrEmpty(p.manytomany.mappedBy)) {
					createJoinTableMany(p, className, collection);
				}
				break;
			case ELEMENT_COLLECTION:
				TableModel em = TableModel.create(createTableId(p.getCollectionTable(),
						className.substring(className.lastIndexOf('.') + 1) + "_" + p.name));
				em = addColumns(em, null, p.collectionTableProperties);

				List<JpaProperty> idProperties2 = collection.stream().filter((q) -> q.id).collect(Collectors.toList());
				em = addKeyReference(em, idProperties2,
						p.getCollectionTable() != null ? p.getCollectionTable().joinColumns : null,
						(oname) -> className.substring(className.lastIndexOf('.') + 1) + "_" + columnName(oname));

				if (p.orderColumn != null) {
					String name;

					if (!Strings.isNullOrEmpty(p.orderColumn.name)) {
						name = p.orderColumn.name;
					} else {
						name = p.name + "_ORDER";
					}

					cm = ColumnModel.create(ColumnId.create(em.getId(), name));
					em = TableModel.Transformations.addColumn(em, cm);
				}

				dm = dm.addTable(em);
				break;
			default:
				break;
			}
		}

		return tableModel;
	}

	private TableModel addKeyReference(TableModel em, List<JpaProperty> idProperties,
			List<JoinColumnAnnotation> joinColumns, Function<JpaProperty, String> defaultGenerator) {
		TableId tableId = em.getId();

		Map<String, String> jc;

		if (joinColumns != null) {
			jc = new HashMap<>();
			for (JoinColumnAnnotation j : joinColumns) {
				jc.put(j.referencedColumnName != null ? j.referencedColumnName.toLowerCase() : null, j.name);
			}
		} else {
			jc = Collections.emptyMap();
		}

		for (JpaProperty q : idProperties) {

			String oname = columnName(q);
			// String name = className.substring(className.lastIndexOf('.') + 1)
			// + "_" + oname;
			String name = defaultGenerator.apply(q);

			if (idProperties.size() == 1) {
				switch (jc.size()) {
				case 0:
					break;
				case 1:
					name = Iterables.getOnlyElement(jc.values());
					break;
				default:
					throw new Error();
				}
			} else if (idProperties.size() == jc.size()) {
				String newName = jc.get(oname.toLowerCase());
				if (newName == null)
					throw new Error("name=" + name + " " + jc);
				name = newName;
			} else if (jc.size() != 0) {
				throw new Error();
			}
			ColumnModel cm = ColumnModel.create(ColumnId.create(em.getId(), name));
			em = TableModel.Transformations.addColumn(em, cm);

		}

		return em;
	}

	public static DatabaseModel create(ClassHierarchy ch) {
		return new DataModelCreator(ch).create2();
	}

	public DatabaseModel create2() {

		dm = DatabaseModel.create();

		for (ClassModel c : ch.getClasses()) {
			JpaAnalysisResult r = c.get(JpaAnalysisResult.class);
			if (r == null)
				continue;

			if (!r.isEntity())
				continue;

			TableId t = createTableId(r.getTable(),
					r.clazz.type.getClassName().substring(r.clazz.type.getClassName().lastIndexOf('.') + 1));

			TableModel tableModel = TableModel.create(t);
			tableModel = addColumns(tableModel, r.clazz.type.getClassName(), r.properties.values());

			dm = dm.addTable(tableModel);
		}

		return dm;
	}
}
