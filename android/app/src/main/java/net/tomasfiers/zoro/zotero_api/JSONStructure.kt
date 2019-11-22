package net.tomasfiers.zoro.zotero_api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

val jsonParserBuilder = Moshi.Builder()
    .add(KotlinJsonAdapterFactory()) // This should come after custom adapters.
    .build()!!
