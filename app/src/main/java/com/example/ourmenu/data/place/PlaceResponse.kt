package com.example.ourmenu.data.place

import com.example.ourmenu.data.ErrorResponse

// place/search, place/search-history
data class PlaceSearchResponse(
    val isSuccess: Boolean,
    val response: ArrayList<PlaceSearchData>,
    val errorResponse: ErrorResponse?,
)

// place/{id}
data class PlaceDetailResponse(
    val isSuccess: Boolean,
    val response: PlaceDetailData,
    val errorResponse: ErrorResponse?,
)
