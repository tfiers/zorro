package net.tomasfiers.zoro.zotero_api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private val httpClientBuilder = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(jsonParserBuilder))
    .baseUrl("https://api.zotero.org/users/4670453/")
    .build()

const val MAX_ITEMS_PER_RESPONSE = 100

interface ZoteroAPIClient {
    @Headers(
        "Zotero-API-Version: 3",
        "Zotero-API-Key: $ZOTERO_API_KEY"
    )
    @GET("collections")
    suspend fun getSomeCollections(
        @Query("start") startIndex: Int = 100,
        @Query("limit") amount: Int = MAX_ITEMS_PER_RESPONSE
    ):
            Response<List<CollectionJSON>>
}

// "lazy" computes value only on first access (client creation is expensive).
val zoteroAPIClient: ZoteroAPIClient by lazy {
    httpClientBuilder.create(ZoteroAPIClient::class.java)
}
