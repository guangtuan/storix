package com.storix.app.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface PublicImageApi {
    @GET
    suspend fun search(@Url url: String): WikipediaSearchResponse

    @GET
    suspend fun summary(@Url url: String): WikipediaSummaryResponse

    companion object {
        fun create(): PublicImageApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PublicImageApi::class.java)
        }
    }
}

data class WikipediaSearchResponse(
    val query: WikipediaQuery? = null
)

data class WikipediaQuery(
    val search: List<WikipediaSearchItem> = emptyList()
)

data class WikipediaSearchItem(
    val title: String = ""
)

data class WikipediaSummaryResponse(
    val thumbnail: WikipediaImage? = null,
    @SerializedName("originalimage")
    val originalImage: WikipediaImage? = null
)

data class WikipediaImage(
    val source: String? = null
)
