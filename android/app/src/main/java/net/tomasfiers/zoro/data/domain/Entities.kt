package net.tomasfiers.zoro.data.domain

import androidx.room.ColumnInfo
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

// We need separately named "key" columns to be able to make many-to-many associations between
// Collection and Item (see Associations.kt). Idem for "name" columns below.
@Entity
data class Collection(
    @PrimaryKey @ColumnInfo(name = "collectionKey")
    override val key: String,
    override var name: String,
    var version: Int,
    var parentKey: String?
) : TreeItem

@Entity
data class Item(
    @PrimaryKey @ColumnInfo(name = "itemKey")
    override var key: String,
    override var name: String,
    var dateAdded: OffsetDateTime,
    var dateModified: OffsetDateTime
) : TreeItem

@Entity
data class ItemType(
    @PrimaryKey @ColumnInfo(name = "itemTypeName")
    val name: String,
    var friendlyName: String
)

@Entity
data class Field(
    @PrimaryKey @ColumnInfo(name = "fieldName")
    val name: String,
    var friendlyName: String,
    var baseField: String? = null
)

@Entity
data class CreatorType(
    @PrimaryKey @ColumnInfo(name = "creatorTypeName")
    val name: String,
    var friendlyName: String
)

// Note: a default value of 0 means "autogenerate when not given on Insert".
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
