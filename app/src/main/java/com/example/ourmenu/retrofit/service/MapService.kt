package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.map.response.MapResponse
import com.example.ourmenu.data.map.response.MapSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("/map")
    fun getMapInfo(): Call<MapResponse>

    @GET("/map/search")
    fun getMapSearch(
        @Query("title") title: String,
    ): Call<MapSearchResponse>
}
