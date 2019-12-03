package net.tomasfiers.zoro.zotero_api

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val zoteroHTTPClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        chain.proceed(
            chain.request().newBuilder()
                .addHeader("Zotero-API-Version", "3")
                .addHeader("Zotero-API-Key", ZOTERO_API_KEY)
                .build()
        )
    }
    .build()

private val zoteroAPIClientCreator = Retrofit.Builder()
    .client(zoteroHTTPClient)
    .baseUrl("https://api.zotero.org/users/4670453/")
    .addConverterFactory(MoshiConverterFactory.create(jsonParserBuilder))
    .build()

const val MAX_ITEMS_PER_RESPONSE = 100

interface ZoteroAPIClient {
    @GET("collections")
    suspend fun getSomeCollections(
        @Query("start") startIndex: Int = 100,
        @Query("limit") amount: Int = MAX_ITEMS_PER_RESPONSE
    ):
            Response<List<CollectionJSON>>
}

// "lazy" computes value only on first access (client creation is expensive).
val zoteroAPIClient: ZoteroAPIClient by lazy {
    zoteroAPIClientCreator.create(ZoteroAPIClient::class.java)
}
