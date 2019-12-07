package net.tomasfiers.zoro.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

interface TreeItem {
    val id: String
    var version: Long
    var name: String
}

@Entity
data class Collection(
    @PrimaryKey
    override val id: String,
    override var version: Long,
    override var name: String,
    var parentId: String?
) : TreeItem

@Entity
data class Item(
    @PrimaryKey
    override val id: String,
    override var version: Long,
    override var name: String,
    var type: ItemType,
    var dateAdded: OffsetDateTime,
    var dateModified: OffsetDateTime
) : TreeItem, ItemMixin()

@Entity
data class Creator(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var firstName: String?,
    var lastName: String?,
    var name: String?
)
