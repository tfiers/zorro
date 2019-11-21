package net.tomasfiers.zoro.zotero_api

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class CollectionJSON(
    val data: Data
) {
    data class Data(
        val key: String,
        val version: Long,
        val name: String,
        @FalseOrString val parentCollection: String?
    )
}

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class FalseOrString

class FalseOrStringAdapter {
    @ToJson
    fun toJSON(@FalseOrString text: String?): Any {
        return when (text) {
            null -> false
            else -> text
        }
    }

    @FromJson
    @FalseOrString
    fun fromJSON(json: Any): String? {
        return when (json) {
            "false" -> null
            else -> json.toString()
        }
    }
}

val jsonParserBuilder = Moshi.Builder()
    .add(FalseOrStringAdapter())
    .add(KotlinJsonAdapterFactory()) // This should be after custom adapters.
    .build()!!
