package net.tomasfiers.zoro.data.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Collection(
    @PrimaryKey
    override val id: String,
    override var version: Long,
    override var name: String,
    var parentId: String?
) : TreeItem
