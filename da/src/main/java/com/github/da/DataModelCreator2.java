package com.github.da;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.objectweb.asm.Type;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import sql.ColumnId;
import sql.ColumnModel;
import sql.DatabaseModel;
import sql.SchemaId;
import sql.TableId;
import sql.TableModel;
import sql.types.IntType;
import sql.types.SqlType;

public class DataModelCreator2 implements com.github.da.t.RootAnalysis {
	@Inject
	ClassHierarchy2 ch;

	@Inject
	Resolver resolve;

	List<TypeMapper> typeMappers;

	@Inject
	void setConfig(DataModelCreatorConfig config) {
		List<TypeMapper> typeMappers = new LinkedList<>();
		config.getTypeMappers().stream().map(p -> resolve.resolve(p)).forEach(typeMappers::add);
		// mappers.add();
		this.typeMappers = typeMappers;
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

		ClassData other = ch.get(p.elementType);
		if (other == null)
			throw new Error();
		JpaAnalysisResult2 ja = other.get(JpaAnalysisResult2.class);
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

		updateTable(em);
	}

	void createJoinTableMany(JpaProperty p, String className, Collection<JpaProperty> collection) {

		ClassData other = ch.get(p.elementType);
		if (other == null)
			throw new Error();
		JpaAnalysisResult2 ja = other.get(JpaAnalysisResult2.class);
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

		updateTable(em);
	}

	ColumnModel.Builder addColumnType(ColumnModel.Builder builder, JpaProperty p) {
		if (p.type == null || p.type2 == null)
			throw new Error();

		SqlType t = null;
		for (TypeMapper typeMapper : typeMappers) {
			t = typeMapper.map(ch, p);
			if (t != null)
				break;
		}
		if (t == null)
			throw new Error("unmapped: " + p);

		if (t != null)
			builder = builder.withType(t);

		return builder;
	}

	public TableModel addColumns(TableModel tableModel, String className, Collection<JpaProperty> collection) {
		for (JpaProperty p : collection) {
			switch (p.fieldType) {
			case VALUE:
				ColumnModel.Builder b = ColumnModel.newBuilder();
				b = b.withId(columnId(tableModel.getId(), p));
				b = addColumnType(b, p);
				tableModel = TableModel.Transformations.addColumn(tableModel, b.build());
				break;
			case ONE_TO_ONE:
				ClassData other = ch.get(p.type);
				if (other == null)
					throw new Error();
				JpaAnalysisResult2 ja = other.get(JpaAnalysisResult2.class);
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
				ja = other.get(JpaAnalysisResult2.class);
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
				other = ch.get(p.elementType);
				if (other == null)
					throw new Error("no type : " + p.elementType);
				ja = other.get(JpaAnalysisResult2.class);
				if (ja == null)
					throw new Error();
				if (!ja.isEntity())
					throw new Error();
				if (p.joinColumns != null) {
					TableId target = createTableId(ja);

					TableModel t1 = TableModel.create(target);
					idProperties = collection.stream().filter((q) -> q.id).collect(Collectors.toList());
					t1 = addKeyReference(t1, idProperties, p.joinColumns, (oname) -> {
						throw new Error();
					});
					updateTable(t1);
				} else if (Strings.isNullOrEmpty(p.onetomany.mappedBy)) {
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

					b = ColumnModel.newBuilder();
					b = b.withId(ColumnId.create(em.getId(), name));
					b = b.withType(IntType.create());
					em = TableModel.Transformations.addColumn(em, b.build());
				}

				updateTable(em);
				break;
			default:
				break;
			}
		}

		return tableModel;
	}

	private TableModel addKeyReference(TableModel em, List<JpaProperty> idProperties,
			List<JoinColumnAnnotation> joinColumns, Function<JpaProperty, String> defaultGenerator) {
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
			String name;

			if (idProperties.size() == 1) {
				switch (jc.size()) {
				case 0:
					name = defaultGenerator.apply(q);
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
					throw new Error("name=" + oname + " " + jc);
				name = newName;
			} else if (jc.size() != 0) {
				throw new Error();
			} else {
				name = defaultGenerator.apply(q);
			}
			ColumnModel.Builder b = ColumnModel.newBuilder();
			b = b.withId(ColumnId.create(em.getId(), name));
			b = addColumnType(b, q);
			em = TableModel.Transformations.addColumn(em, b.build());

		}

		return em;
	}

	TableId createTableId(JpaAnalysisResult2 r) {
		return createTableId(r.getTable(), removeLead(r.clazz.get(Type.class).getClassName()));
	}

	@Inject
	AnalysisResult ar;

	public void run() {

		dm = DatabaseModel.create();

		for (ClassData c : ch.getClasses()) {
			JpaAnalysisResult2 r = c.get(JpaAnalysisResult2.class);
			if (r == null)
				continue;

			if (!r.isEntity())
				continue;

			TableId t = createTableId(r);

			TableModel tableModel = TableModel.create(t);
			tableModel = addColumns(tableModel, r.clazz.get(Type.class).getClassName(), r.properties.values());
			updateTable(tableModel);
		}

		ar.put(DatabaseModel.class, dm);
	}

	private void updateTable(TableModel tableModel) {
		TableModel old = dm.getTable(tableModel.getId());
		tableModel = merge(old, tableModel);

		if (old != null)
			dm = dm.removeTable(old.getId());
		dm = dm.addTable(tableModel);
	}

	private TableModel merge(TableModel t1, TableModel t2) {
		if (t1 != null && t2 == null)
			return t1;
		else if (t1 == null && t2 != null)
			return t2;

		Set<ColumnId> cols = new HashSet<>();

		Map<ColumnId, ColumnModel> cols1 = t1.getColumnsAsMap();
		Map<ColumnId, ColumnModel> cols2 = t2.getColumnsAsMap();

		cols1.values().stream().map(ColumnModel::getId).forEach(cols::add);
		cols2.values().stream().map(ColumnModel::getId).forEach(cols::add);

		TableModel t = TableModel.create(t1.getId());

		for (ColumnId c : cols) {
			ColumnModel c1 = cols1.get(c);
			ColumnModel c2 = cols2.get(c);
			if (c1 != null && c2 == null) {
				t = TableModel.Transformations.addColumn(t, c1);
			} else if (c1 == null && c2 != null) {
				t = TableModel.Transformations.addColumn(t, c2);
			} else {
				t = TableModel.Transformations.addColumn(t, merge(c1, c2));
			}
		}

		return t;
	}

	private ColumnModel merge(ColumnModel c1, ColumnModel c2) {
		Objects.requireNonNull(c1);
		Objects.requireNonNull(c2);

		if (!Objects.equals(c1.getType(), c2.getType()))
			throw new Error();

		return c1;
	}
}
