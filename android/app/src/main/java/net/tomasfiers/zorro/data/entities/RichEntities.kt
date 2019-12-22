package net.tomasfiers.zorro.data.entities

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.Relation


data class ItemWithReferences(
    @Embedded val item: Item,

    @Relation(
        parentColumn = "itemTypeName",
        entityColumn = "name"
    )
    var type: ItemType,

    @Relation(
        parentColumn = "itemTypeName",
        entityColumn = "name",
        associateBy = Junction(
            ItemTypeFieldAssociation::class,
            parentColumn = "itemTypeName",
            entityColumn = "fieldName"
        )
    )
    val fields: List<Field>,

    @Relation(
        parentColumn = "key",
        entityColumn = "itemKey"
    )
    val fieldValues: List<ItemFieldValue>,

    @Relation(
        parentColumn = "itemTypeName",
        entityColumn = "itemTypeName"
    )
    private val itemTypeFieldAssociations: List<ItemTypeFieldAssociation>,

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

) : ListElement {
    @Ignore
    override val key = item.key
    override val name: String
        get() = getFieldValue(titleField?.name) ?: ""

    val sortedFieldValues: List<ItemFieldValue>
        get() = fieldValues.sortedBy { fieldValue ->
            itemTypeFieldAssociations.find { it.fieldName == fieldValue.fieldName }?.orderIndex
        }

    @Ignore
    private val titleField =
        fields.find { it.baseField == "title" }
            ?: fields.find { it.name == "title" }

    private fun getFieldValue(fieldName: String?) =
        fieldValues.find { it.fieldName == fieldName }?.value
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
