package net.tomasfiers.zoro.data

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

//private val jsonParserBuilder = Moshi.Builder()
//    .add(KotlinJsonAdapterFactory())
//    .build()

private val httpClientBuilder = Retrofit.Builder()
//    .addConverterFactory(MoshiConverterFactory.create(jsonParserBuilder))
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl("https://api.zotero.org/users/4670453/")
    .build()

interface ZoteroAPIClient {
    @Headers(
        "Zotero-API-Version: 3",
        "Zotero-API-Key: $ZOTERO_API_KEY"
    )
    @GET("collections")
    fun getCollections():
            Call<String>
}

// Use singleton because client creation is expensive.
// ("lazy" computes property only on first access).
object ZoteroAPI {
    val client: ZoteroAPIClient by lazy {
        httpClientBuilder.create(ZoteroAPIClient::class.java)
    }
}
