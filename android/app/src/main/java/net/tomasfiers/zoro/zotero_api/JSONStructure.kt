package net.tomasfiers.zoro.zotero_api

import com.squareup.moshi.JsonClass
import net.tomasfiers.zoro.data.Collection

@JsonClass(generateAdapter = true)
data class CollectionJSON(
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        val key: String,
        val version: Long,
        val name: String,
        val parentCollection: Any
    )

    fun asDomainModel() = Collection(
        collectionId = data.key,
        version = data.version,
        name = data.name,
        parentId = when (data.parentCollection) {
            false -> null
            else -> data.parentCollection as String
        }
    )
}
