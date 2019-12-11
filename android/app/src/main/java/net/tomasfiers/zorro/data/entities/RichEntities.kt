package net.tomasfiers.zorro.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CollectionWithItems(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = "key",
        entityColumn = "key",
        associateBy = Junction(
            ItemCollectionAssociation::class,
            parentColumn = "collectionKey",
            entityColumn = "itemKey"
        )
    )
    val items: List<Item>
)


data class ItemWithReferences(
    @Embedded val item: Item,

    @Relation(
        parentColumn = "itemTypeName",
        entityColumn = "name"
    )
    var type: ItemType,

    @Relation(
        parentColumn = "key",
        entityColumn = "itemKey"
    )
    val fieldValues: List<ItemFieldValue>,

    @Relation(
        parentColumn = "key",
        entityColumn = "key",
        associateBy = Junction(
            ItemCollectionAssociation::class,
            parentColumn = "itemKey",
            entityColumn = "collectionKey"
        )
    )
    val collections: List<Collection>,

    @Relation(
        parentColumn = "key",
        entityColumn = "itemKey"
    )
    val creators: List<Creator>

) : TreeItem {
    override val key = item.key
    override var name: String
        get() = getTitle()
        set(value) {}

    //val titleField: String = fieldValues.map { it. }

    fun getTitle(): String {
        return TODO()
    }
}


data class ItemTypeWithReferences(
    @Embedded val itemType: ItemType,

    @Relation(
        parentColumn = "name",
        entityColumn = "name",
        associateBy = Junction(
            ItemTypeFieldAssociation::class,
            parentColumn = "itemTypeName",
            entityColumn = "fieldName"
        )
    )
    var fields: List<Field>,

    @Relation(
        parentColumn = "name",
        entityColumn = "name",
        associateBy = Junction(
            ItemTypeCreatorTypeAssociation::class,
            parentColumn = "itemTypeName",
            entityColumn = "creatorTypeName"
        )
    )
    var creatorTypes: List<CreatorType>
)
