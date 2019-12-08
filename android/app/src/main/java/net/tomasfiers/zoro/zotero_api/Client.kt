package net.tomasfiers.zoro.zotero_api

import net.tomasfiers.zoro.util.createJsonHttpClient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// "lazy" computes value only on first access (client creation is expensive).
val zoteroAPIClient by lazy {
    createJsonHttpClient(
        baseUrl = "https://api.zotero.org/",
        requestHeaders = mapOf(
            "Zotero-API-Version" to "3",
            "Zotero-API-Key" to ZOTERO_API_KEY
        ),
        APIInterface = ZoteroAPIClient::class.java
    )
}

private const val USER_PREFIX = "users/4670453/"

interface ZoteroAPIClient {

    @GET("schema")
    suspend fun getSchema(): Response<SchemaJson>

    @GET("${USER_PREFIX}collections?format=versions")
    suspend fun getCollectionVersions(
        @Query("since") sinceLibraryVersion: Int = 0
    ): Response<Map<String, Int>>

    @GET("${USER_PREFIX}items?format=versions")
    suspend fun getItemVersions(
        @Query("since") sinceLibraryVersion: Int = 0
    ): Response<Map<String, Int>>

    // `collectionIds` should be a comma separated list.
    @GET("${USER_PREFIX}collections")
    suspend fun getCollections(
        @Query("collectionKey") collectionIds: String,
        @Query("limit") amount: Int = MAX_ITEMS_PER_RESPONSE
    ): Response<List<CollectionJson>>

    // `itemIds` should be a comma separated list.
    @GET("${USER_PREFIX}items")
    suspend fun getItems(
        @Query("itemKey") itemIds: String,
        @Query("limit") amount: Int = MAX_ITEMS_PER_RESPONSE
    ): Response<List<CollectionJson>>
}

val <T> Response<T>.remoteLibraryVersion: Int?
    get() = this.headers()["Last-Modified-Version"]?.toInt()

// How many items or collections can maximally be retrieved per request.
// This limit does not apply when "?format=versions".
const val MAX_ITEMS_PER_RESPONSE = 100
