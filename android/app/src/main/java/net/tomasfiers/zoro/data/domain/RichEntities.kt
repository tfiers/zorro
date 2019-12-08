package net.tomasfiers.zoro.data.domain

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CollectionWithItems(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = "collectionKey",
        entityColumn = "itemKey",
        associateBy = Junction(ItemCollectionAssociation::class)
    )
    val items: List<Item>
)


data class ItemWithReferences(
    @Embedded val item: Item,

    @Relation(
        parentColumn = "itemKey",
        entityColumn = "itemTypeName"
    )
    var type: ItemType,

    @Relation(
        parentColumn = "itemKey",
        entityColumn = "collectionKey",
        associateBy = Junction(ItemCollectionAssociation::class)
    )
    val collections: List<Collection>,

    @Relation(
        parentColumn = "itemKey",
        entityColumn = "creatorId",
        associateBy = Junction(ItemCreatorAssociation::class)
    )
    val creators: List<Creator>
)


data class ItemTypeWithReferences(
    @Embedded val itemType: ItemType,

    @Relation(
        parentColumn = "itemTypeName",
        entityColumn = "fieldName",
        associateBy = Junction(ItemTypeFieldAssociation::class)
    )
    var fields: List<Field>,

    @Relation(
        parentColumn = "itemTypeName",
        entityColumn = "creatorTypeName",
        associateBy = Junction(ItemTypeCreatorTypeAssociation::class)
    )
    var creatorTypes: List<CreatorType>
)
