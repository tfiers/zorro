package net.tomasfiers.zoro.data.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["collectionId", "itemId"])
data class ItemCollectionAssociation(
    val collectionId: Int,
    val itemId: Int
)

@Entity(primaryKeys = ["itemId", "fieldId", "itemDataValueId"])
data class ItemDataAssociation(
    val itemId: Int,
    val fieldId: Int,
    val itemDataValueId: Int
)

@Entity(primaryKeys = ["itemId", "creatorId"])
data class ItemCreatorAssociation(
    val itemId: Int,
    val creatorId: Int
)

@Entity(primaryKeys = ["itemTypeId", "fieldId"])
data class ItemTypeFieldAssociation(
    val itemTypeId: Int,
    val fieldId: Int
)

@Entity(primaryKeys = ["itemTypeId", "creatorTypeId"])
data class ItemTypeCreatorTypeAssociation(
    val itemTypeId: Int,
    val creatorTypeId: Int
)


data class CollectionWithItems(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = "collectionId",
        entityColumn = "itemId",
        associateBy = Junction(ItemCollectionAssociation::class)
    )
    val items: List<Item>
)

data class ItemWithReferences(
    @Embedded val item: Item,

    @Relation(
        parentColumn = "itemId",
        entityColumn = "collectionId",
        associateBy = Junction(ItemCollectionAssociation::class)
    )
    val collections: List<Collection>,

    @Relation(
        parentColumn = "itemId",
        entityColumn = "creatorId",
        associateBy = Junction(ItemCreatorAssociation::class)
    )
    val creators: List<Creator>
)

data class ItemTypeWithReferences(
    @Embedded val itemType: ItemType,

    @Relation(
        parentColumn = "itemTypeId",
        entityColumn = "fieldId",
        associateBy = Junction(ItemTypeFieldAssociation::class)
    )
    var fields: List<Field>,

    @Relation(
        parentColumn = "itemTypeId",
        entityColumn = "creatorTypeId",
        associateBy = Junction(ItemTypeCreatorTypeAssociation::class)
    )
    var creatorTypes: List<CreatorType>
)
