/**
 * Helper classes for many-to-many associations between Entities.
 */

package net.tomasfiers.zorro.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE


@Entity(primaryKeys = ["collectionKey", "itemKey"])
data class ItemCollectionAssociation(
    val collectionKey: String,
    val itemKey: String
)


// Delete association objects when corresponding ItemType is deleted.
// (Btw we cannot factor out this ForeignKey -- it's some kind of annotation, not a simple value).
@Entity(
    primaryKeys = ["itemTypeName", "fieldName"],
    foreignKeys = [ForeignKey(
        entity = ItemType::class,
        parentColumns = ["name"],
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
        parentColumns = ["name"],
        childColumns = ["itemTypeName"],
        onDelete = CASCADE
    )]
)
data class ItemTypeCreatorTypeAssociation(
    val itemTypeName: String,
    val creatorTypeName: String,
    val primary: Boolean
)
