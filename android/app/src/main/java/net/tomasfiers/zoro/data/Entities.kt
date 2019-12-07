package net.tomasfiers.zoro.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
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
// Collection and Item (see ItemCollectionAssociation).
@Entity
data class Collection(
    @PrimaryKey(autoGenerate = true)
    val collectionId: Int,
    override val key: String,
    override var name: String,
    var version: Int,
    var parentId: String?
) : TreeItem

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int,
    override var key: String,
    override var name: String,
    var type: ItemType,
    var dateAdded: OffsetDateTime,
    var dateModified: OffsetDateTime,

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
) : TreeItem

@Entity
data class ItemType(
    @PrimaryKey(autoGenerate = true)
    val itemTypeId: Int,
    val name: String,
    var friendlyName: String,

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

@Entity
data class Field(
    @PrimaryKey(autoGenerate = true)
    val fieldId: Int,
    val name: String,
    var friendlyName: String
)

@Entity
data class ItemDataValue(
    @PrimaryKey(autoGenerate = true)
    val dataValueId: Int,
    var value: String
)

@Entity
data class Creator(
    @PrimaryKey(autoGenerate = true)
    val creatorId: Int,
    var firstName: String?,
    var lastName: String?
)

@Entity
data class CreatorType(
    @PrimaryKey(autoGenerate = true)
    val creatorTypeId: Int,
    val name: String,
    var friendlyName: String
)


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


// We cannot reference items in Collection itself, as this would create a recursive reference (as
// Item already references collections).
data class CollectionWithItems(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = "collectionId",
        entityColumn = "itemId",
        associateBy = Junction(ItemCollectionAssociation::class)
    )
    val items: List<Item>
)
