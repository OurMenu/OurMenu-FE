package com.example.ourmenu.data.map.response

import com.example.ourmenu.data.ErrorResponse
import com.example.ourmenu.data.map.data.MapData
import com.example.ourmenu.data.map.data.MapInfoDetailData
import com.example.ourmenu.data.map.data.MapSearchData

data class MapResponse(
    val errorResponse: ErrorResponse,
    val isSuccess: Boolean,
    val response: ArrayList<MapData>,
)

data class MapSearchResponse(
    val errorResponse: ErrorResponse,
    val isSuccess: Boolean,
    val response: ArrayList<MapSearchData>,
)

data class MapInfoDetailResponse(
    val errorResponse: ErrorResponse,
    val isSuccess: Boolean,
    val response: ArrayList<MapInfoDetailData>,
)
