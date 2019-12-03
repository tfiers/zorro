package net.tomasfiers.zoro.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// Wrapper to encapsulate OkHttp / Retrofit / Moshi syntax.
// Call as `createAPIClient(..., MyInterface::class.java)`
fun <T> createJSONAPIClient(
    baseUrl: String,
    requestHeaders: Map<String, String>,
    APIInterface: Class<T>
): T {
    val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            for ((k, v) in requestHeaders) {
                requestBuilder.addHeader(k, v)
            }
            chain.proceed(requestBuilder.build())
        }
        .build()
    val jsonParser = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // This should come after custom adapters.
        .build()!!
    return Retrofit.Builder()
        .client(httpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(jsonParser))
        .build()
        .create(APIInterface)
}
