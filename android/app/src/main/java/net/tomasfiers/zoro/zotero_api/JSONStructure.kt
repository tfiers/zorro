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
        val version: Int,
        val name: String,
        val parentCollection: Any
    )

    fun asDomainModel() = Collection(
        key = data.key,
        version = data.version,
        name = data.name,
        parentKey = when (data.parentCollection) {
            false -> null
            else -> data.parentCollection as String
        }
    )
}
