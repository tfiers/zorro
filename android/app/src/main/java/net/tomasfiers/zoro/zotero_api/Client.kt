package net.tomasfiers.zoro.zotero_api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private val httpClientBuilder = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(jsonParserBuilder))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl("https://api.zotero.org/users/4670453/")
    .build()

interface ZoteroAPIClient {
    @Headers(
        "Zotero-API-Version: 3",
        "Zotero-API-Key: $ZOTERO_API_KEY"
    )
    @GET("collections")
    fun getSomeCollections(
        @Query("start") startIndex: Int = 100,
        @Query("limit") numItems: Int = 100
    ):
            Deferred<List<CollectionJSON>>
}

// Use singleton because client creation is expensive.
// ("lazy" computes property only on first access).
object ZoteroAPI {
    val client: ZoteroAPIClient by lazy {
        httpClientBuilder.create(ZoteroAPIClient::class.java)
    }
}
