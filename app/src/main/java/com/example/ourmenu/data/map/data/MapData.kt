package com.example.ourmenu.data.map.data

data class MapData(
    val groupId: Int,
    val latitude: Double,
    val longitude: Double,
    val menuIconType: String,
    val menuTitle: String,
    val placeId: Int,
)

data class MapSearchData(
    val groupId: Int,
    val menuTitle: String,
    val placeAddress: String,
    val placeTitle: String,
)
