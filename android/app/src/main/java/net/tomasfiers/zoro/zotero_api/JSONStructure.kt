package net.tomasfiers.zoro.zotero_api

import net.tomasfiers.zoro.data.Collection

data class CollectionJSON(
    val data: Data
) {
    data class Data(
        val key: String,
        val version: Long,
        val name: String,
        val parentCollection: Any
    )

    fun asDomainModel() = Collection(
        id = data.key,
        version = data.version,
        name = data.name,
        parentId = when (data.parentCollection) {
            false -> null
            else -> data.parentCollection as String
        }
    )
}
