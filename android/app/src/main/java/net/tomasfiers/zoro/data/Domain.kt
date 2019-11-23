package net.tomasfiers.zoro.data

interface TreeItem {
    val id: String
    var version: Long?
    var name: String?
}

data class Collection(
    override val id: String,
    override var version: Long?,
    override var name: String?,
    var parentId: String?
) : TreeItem
