package net.tomasfiers.zorro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


/**
 * Used for storing app settings and other arbitrary one-row data.
 */
@Entity
data class KeyValPair(
    @PrimaryKey
    val key: String,
    val value: String?
)


// Keys will probably not overlap between Collections and Items. A Zotero key has 8 alphanumeric
// characters. 8^36 unique keys = 2821 billion = 10^12 = 1/10 of cells in human body.
// Similarly, Int's are OK for ID's: https://stackoverflow.com/a/55995219/2611913.
// 2^32 / 2 = 2 billion. (Android dev docs use Ints too).
interface ListElement {
    val key: String
    val name: String
}

@Entity
data class Collection(
    @PrimaryKey
    override val key: String,
    override val name: String,
    var parentKey: String?
) : ListElement

@Entity
data class Item(
    @PrimaryKey
    val key: String,
    var itemTypeName: String,
    var dateAdded: OffsetDateTime,
    var dateModified: OffsetDateTime
)

@Entity
data class ItemType(
    @PrimaryKey
    val name: String,
    var friendlyName: String
)

@Entity
data class Field(
    @PrimaryKey
    val name: String,
    var friendlyName: String,
    var baseField: String? = null
)

@Entity
data class CreatorType(
    @PrimaryKey
    val name: String,
    var friendlyName: String
)

@Entity(primaryKeys = ["itemKey", "fieldName"])
data class ItemFieldValue(
    val itemKey: String,
    val fieldName: String,
    var value: String
)

@Entity
data class Creator(
    var firstName: String?,
    var lastName: String?,
    var name: String?,
    val itemKey: String,
    val type: String,
    // Note: a default value of 0 means "autogenerate when not given on Insert".
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
