package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class RemoteCourseItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "city") val city: String,
    @Json(name = "state") val state: String,
    @Json(name = "country") val country: String = "USA",
    @Json(name = "holes_count") val holesCount: Int = 18,
    @Json(name = "course_type") val courseType: String = "Championship",
    @Json(name = "rating") val rating: Double = 72.0,
    @Json(name = "slope") val slope: Int = 125,
    @Json(name = "par") val par: Int = 72,
    @Json(name = "yardage") val yardage: Int = 6500,
    @Json(name = "pars") val pars: List<Int>? = null,
    @Json(name = "handicap_ranks") val handicapRanks: List<Int>? = null
)

interface GolfCourseApiService {

    @GET("api/v1/courses/search")
    suspend fun searchCourses(
        @Query("query") query: String,
        @Query("country") country: String = "US"
    ): List<RemoteCourseItem>

    @GET("api/v1/courses/{id}")
    suspend fun getCourseDetails(
        @Path("id") courseId: String
    ): RemoteCourseItem

    companion object {
        private const val BASE_URL = "https://golf-courses-api.p.rapidapi.com/"

        fun create(): GolfCourseApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(GolfCourseApiService::class.java)
        }
    }
}
