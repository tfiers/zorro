package net.tomasfiers.zoro.data.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

// Keys will probably not overlap between Collections and Items. A Zotero key has 8 alphanumeric
// characters. 8^36 unique keys = 2821 billion = 10^12 = 1/10 of cells in human body.
// Similarly, Int's are OK for ID's: https://stackoverflow.com/a/55995219/2611913.
// 2^32 / 2 = 2 billion. (Android dev docs use Ints too).
interface TreeItem {
    val key: String
    var name: String
}

// We need separately named "id" columns to be able to make many-to-many associations between
// Collection and Item (see Associations.kt).
@Entity
data class Collection(
    @PrimaryKey(autoGenerate = true)
    val collectionId: Int = 0,
    override val key: String,
    override var name: String,
    var version: Int,
    var parentKey: String?
) : TreeItem

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,
    override var key: String,
    override var name: String,
    var type: ItemType,
    var dateAdded: OffsetDateTime,
    var dateModified: OffsetDateTime
) : TreeItem

@Entity
data class ItemType(
    @PrimaryKey(autoGenerate = true)
    val itemTypeId: Int = 0,
    val name: String,
    var friendlyName: String
)

@Entity
data class Field(
    @PrimaryKey(autoGenerate = true)
    val fieldId: Int = 0,
    val name: String,
    var friendlyName: String
)

@Entity
data class ItemDataValue(
    @PrimaryKey(autoGenerate = true)
    val dataValueId: Int = 0,
    var value: String
)

@Entity
data class Creator(
    @PrimaryKey(autoGenerate = true)
    val creatorId: Int = 0,
    var firstName: String?,
    var lastName: String?
)

@Entity
data class CreatorType(
    @PrimaryKey(autoGenerate = true)
    val creatorTypeId: Int = 0,
    val name: String,
    var friendlyName: String
)
