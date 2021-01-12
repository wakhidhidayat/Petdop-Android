package com.wahidhidayat.petdop.network

import com.wahidhidayat.petdop.data.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndpoints {
    @GET("everything")
    fun getNews(
            @Query("q") query: String,
            @Query("apiKey") apiKey: String
    ): Call<News>
}