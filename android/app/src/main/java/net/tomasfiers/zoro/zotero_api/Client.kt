package net.tomasfiers.zoro.zotero_api

import net.tomasfiers.zoro.util.createJSONAPIClient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.zotero.org/users/4670453/"
private val REQUEST_HEADERS = mapOf(
    "Zotero-API-Version" to "3",
    "Zotero-API-Key" to ZOTERO_API_KEY
)
const val MAX_ITEMS_PER_RESPONSE = 100

interface ZoteroAPIClient {
    @GET("collections")
    suspend fun getSomeCollections(
        @Query("start") startIndex: Int = 0,
        @Query("limit") amount: Int = MAX_ITEMS_PER_RESPONSE
    ):
            Response<List<CollectionJSON>>
}

// "lazy" computes value only on first access (client creation is expensive).
val zoteroAPIClient by lazy {
    createJSONAPIClient(BASE_URL, REQUEST_HEADERS, ZoteroAPIClient::class.java)
}
