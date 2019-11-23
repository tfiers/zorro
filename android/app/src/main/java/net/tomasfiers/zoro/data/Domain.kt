package net.tomasfiers.zoro.data

open class TreeItem() {
    val id: String? = null
    var version: Long? = null
    var name: String? = null
}

data class Collection(
    var parentId: String?
) : TreeItem()
