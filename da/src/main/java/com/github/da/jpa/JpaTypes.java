package com.github.da.jpa;

import org.objectweb.asm.Type;

public class JpaTypes {

	static final Type embeddable = Type.getObjectType("javax/persistence/Embeddable");
	static final Type entity = Type.getObjectType("javax/persistence/Entity");
	static final Type javaxPersistenceTable = Type.getObjectType("javax/persistence/Table");
	static final Type javaxPersistenceAccess = Type.getObjectType("javax/persistence/Access");
	static final Type cacheable = Type.getObjectType("javax/persistence/Cacheable");
	static final Type mappedSuperclass = Type.getObjectType("javax/persistence/MappedSuperclass");
	static final Type idClass = Type.getObjectType("javax/persistence/IdClass");
	static final Type entityListeners = Type.getObjectType("javax/persistence/EntityListeners");
	static final Type javaxPersistenceColumn = Type.getObjectType("javax/persistence/Column");
	static final Type javaxPersistenceEmbedded = Type.getObjectType("javax/persistence/Embedded");
	static final Type javaxPersistenceTransient = Type.getObjectType("javax/persistence/Transient");
	static final Type javaxPersistenceAttributeOverride = Type.getObjectType("javax/persistence/AttributeOverride");
	static final Type javaxPersistenceAttributeOverrides = Type.getObjectType("javax/persistence/AttributeOverrides");
	static final Type javaxPersistenceEnumerated = Type.getObjectType("javax/persistence/Enumerated");
	static final Type javaxPersistenceManyToOne = Type.getObjectType("javax/persistence/ManyToOne");
	static final Type javaxPersistenceJoinColumn = Type.getObjectType("javax/persistence/JoinColumn");
	static final Type javaxPersistenceOneToOne = Type.getObjectType("javax/persistence/OneToOne");
	static final Type javaxPersistenceOneToMany = Type.getObjectType("javax/persistence/OneToMany");
	static final Type javaxPersistenceBasic = Type.getObjectType("javax/persistence/Basic");
	static final Type javaxPersistenceOrderColumn = Type.getObjectType("javax/persistence/OrderColumn");
	static final Type javaxPersistenceId = Type.getObjectType("javax/persistence/Id");
	static final Type javaxPersistenceSequenceGenerator = Type.getObjectType("javax/persistence/SequenceGenerator");
	static final Type javaxPersistenceGeneratedValue = Type.getObjectType("javax/persistence/GeneratedValue");
	static final Type javaxPersistenceVersion = Type.getObjectType("javax/persistence/Version");
	static final Type javaxPersistenceManyToMany = Type.getObjectType("javax/persistence/ManyToMany");
	static final Type javaxPersistenceJoinTable = Type.getObjectType("javax/persistence/JoinTable");
	static final Type javaxPersistenceEmbeddedId = Type.getObjectType("javax/persistence/EmbeddedId");
	static final Type javaxPersistenceMapsId = Type.getObjectType("javax/persistence/MapsId");
	static final Type javaxPersistenceJoinColumns = Type.getObjectType("javax/persistence/JoinColumns");
	static final Type javaxPersistencePrimaryKeyJoinColumns = Type
			.getObjectType("javax/persistence/PrimaryKeyJoinColumns");
	static final Type javaxPersistenceTemporal = Type.getObjectType("javax/persistence/Temporal");
	static final Type javaxPersistenceOrderBy = Type.getObjectType("javax/persistence/OrderBy");
	static final Type javaxPersistenceLob = Type.getObjectType("javax/persistence/Lob");
	static final Type javaxPersistenceMapKey = Type.getObjectType("javax/persistence/MapKey");
	static final Type javaxPersistenceElementCollection = Type.getObjectType("javax/persistence/ElementCollection");
	static final Type javaxPersistenceCollectionTable = Type.getObjectType("javax/persistence/CollectionTable");
	static final Type javaxPersistencePrePersist = Type.getObjectType("javax/persistence/PrePersist");
	static final Type javaxPersistencePreUpdate = Type.getObjectType("javax/persistence/PreUpdate");
	static final Type javaxPersistencePostPersist = Type.getObjectType("javax/persistence/PostPersist");
	static final Type javaxPersistencePostUpdate = Type.getObjectType("javax/persistence/PostUpdate");
	static final Type javaxPersistencePersistenceContext = Type.getObjectType("javax/persistence/PersistenceContext");

}