package com.example.ourmenu.data

data class PlaceInfoData(
    val placeName: String,
    val type: String,
    val distance: String,
    val recentSearch: Boolean = false,
    val address: String,
    val time: String,
    val imgs: ArrayList<Int>,
    val menus: ArrayList<PlaceMenuData>,
    val mapx: String,
    val mapy: String,
)

data class PlaceMenuData(
    val menuName: String,
    val price: String, // 아니면 int?
)
