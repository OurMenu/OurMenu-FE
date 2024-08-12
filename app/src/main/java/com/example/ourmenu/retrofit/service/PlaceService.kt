package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.place.PlaceDetailResponse
import com.example.ourmenu.data.place.PlaceSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceService {
    @GET("place/search")
    fun getPlaceInfo(
        @Query("title") title: String,
    ): Call<PlaceSearchResponse>

    @GET("place/search-history")
    fun getPlaceSearchHistory(): Call<PlaceSearchResponse>

    @GET("place/search/{id}")
    fun getPlaceInfoDetail(
        @Path("id") id: String,
    ): Call<PlaceDetailResponse>
}
