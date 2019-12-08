package net.tomasfiers.zoro.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(primaryKeys = ["collectionKey", "itemKey"])
data class ItemCollectionAssociation(
    val collectionKey: String,
    val itemKey: String
)

@Entity(primaryKeys = ["itemKey", "fieldName", "itemDataValueId"])
data class ItemItemDataValueAssociation(
    val itemKey: String,
    val fieldName: String,
    val itemDataValueId: Int
)

@Entity(primaryKeys = ["itemKey", "creatorId"])
data class ItemCreatorAssociation(
    val itemKey: String,
    val creatorId: Int
)


// Delete association objects when corresponding ItemType is deleted.
// (Btw we cannot factor out this ForeignKey -- it's some kind of annotation, not a simple value).
@Entity(
    primaryKeys = ["itemTypeName", "fieldName"],
    foreignKeys = [ForeignKey(
        entity = ItemType::class,
        parentColumns = ["itemTypeName"],
        childColumns = ["itemTypeName"],
        onDelete = CASCADE
    )]
)
data class ItemTypeFieldAssociation(
    val itemTypeName: String,
    val fieldName: String
)

@Entity(
    primaryKeys = ["itemTypeName", "creatorTypeName"],
    foreignKeys = [ForeignKey(
        entity = ItemType::class,
        parentColumns = ["itemTypeName"],
        childColumns = ["itemTypeName"],
        onDelete = CASCADE
    )]
)
data class ItemTypeCreatorTypeAssociation(
    val itemTypeName: String,
    val creatorTypeName: String
)
