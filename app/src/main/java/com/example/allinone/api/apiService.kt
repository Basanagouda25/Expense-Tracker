package com.example.allinone.api

import retrofit2.http.GET
import retrofit2.http.Query


data class CategoryResponse(
    val input: String,
    val category: String
)
interface ApiService {

    @GET("predict")
    suspend fun predictCategory(
        @Query("text") text: String
    ): CategoryResponse
}