package net.tomasfiers.zoro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

interface TreeItem {
    val id: String
    var version: Long?
    var name: String?
}

@Entity
data class Collection(
    @PrimaryKey
    override val id: String,
    override var version: Long?,
    override var name: String?,
    var parentId: String?
) : TreeItem
