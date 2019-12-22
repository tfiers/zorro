package net.tomasfiers.zorro.zotero_api

import com.squareup.moshi.JsonClass
import net.tomasfiers.zorro.data.entities.Collection
import net.tomasfiers.zorro.data.entities.Item
import org.threeten.bp.OffsetDateTime


@JsonClass(generateAdapter = true)
data class SchemaJson(
    val version: Int,
    val itemTypes: List<ItemTypeJson>,
    val meta: MetaJson,
    val locales: Map<String, LocaleJson>
) {

    @JsonClass(generateAdapter = true)
    data class ItemTypeJson(
        val itemType: String,
        val fields: List<FieldJson>,
        val creatorTypes: List<CreatorTypeJson>
    ) {
        @JsonClass(generateAdapter = true)
        data class FieldJson(
            val field: String,
            val baseField: String? = null
        )

        @JsonClass(generateAdapter = true)
        data class CreatorTypeJson(
            val creatorType: String,
            val primary: Boolean? = null
        )
    }

    @JsonClass(generateAdapter = true)
    data class MetaJson(
        val fields: Map<String, MetaFieldTypeJson>
    ) {
        @JsonClass(generateAdapter = true)
        data class MetaFieldTypeJson(
            val type: String
        )
    }

    @JsonClass(generateAdapter = true)
    data class LocaleJson(
        val itemTypes: Map<String, String>,
        val fields: Map<String, String>,
        val creatorTypes: Map<String, String>
    )
}

@JsonClass(generateAdapter = true)
data class CollectionJson(
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        val key: String,
        val name: String,
        val parentCollection: Any
    )

    fun asDomainModel() = Collection(
        key = data.key,
        name = data.name,
        parentKey = when (data.parentCollection) {
            false -> null
            else -> data.parentCollection as String
        }
    )
}

@Suppress("UNCHECKED_CAST")
@JsonClass(generateAdapter = true)
data class ItemJson(val data: Map<String, Any>) {

    val key: String = data["key"] as String

    val collectionKeys =
        if (data.containsKey(COLLECTIONS))
            data[COLLECTIONS] as List<String>
        else null

    val creators =
        if (data.containsKey(CREATORS))
            data[CREATORS] as List<Map<String, String>>?
        else null

    fun asDomainModel() = Item(
        key = key,
        itemTypeName = data[ITEMTYPE] as String,
        dateAdded = OffsetDateTime.parse(data[DATE_ADDED] as String),
        dateModified = OffsetDateTime.parse(data[DATE_MODIFIED] as String)
    )
}
