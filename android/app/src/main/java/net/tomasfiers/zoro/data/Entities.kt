package net.tomasfiers.zoro.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import org.threeten.bp.OffsetDateTime

// IDs will probably not overlap between Collections and Items. An ID has 8 alphanumeric
// characters. 8^36 unique IDs = 2821 billion = 10^12 = 1/10 of cells in human body.
interface TreeItem {
    val id: String
    var version: Long
    var name: String
}

// We need separately named "id" columns to be able to make many-to-many associations between
// Collection and Item (see ItemCollectionAssociation).
@Entity
data class Collection(
    @PrimaryKey
    val collectionId: String,
    override var version: Long,
    override var name: String,
    var parentId: String?
) : TreeItem {
    override val id: String
        get() = collectionId
}

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

@Entity
data class Item(
    @PrimaryKey
    val itemId: String,
    override var version: Long,
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
        entityColumn = "creatorId"
    )
    val creators: List<Creator>

) : TreeItem, ItemMixin() {
    override val id: String
        get() = itemId
}

@Entity
data class Creator(
    @PrimaryKey(autoGenerate = true)
    val creatorId: Int,
    var firstName: String?,
    var lastName: String?,
    var name: String?,
    var type: CreatorType
)

@Entity(primaryKeys = ["collectionId", "itemId"])
data class ItemCollectionAssociation(
    val collectionId: String,
    val itemId: String
)
