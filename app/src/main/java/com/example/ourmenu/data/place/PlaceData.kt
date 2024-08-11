package com.example.ourmenu.data.place

data class PlaceInfoData2(
    val name: String,
    val address: String,
//    val type: String,
    val images: ArrayList<String>,
    val menus: ArrayList<PlaceInfoMenuData>,
    val time: String?,
    val mapx: String,
    val mapy: String,
)

data class PlaceInfoMenuData(
    val name: String,
    val price: String,
)

data class PlaceSearchHistoryData(
    val storeName: String,
    val address: String,
)

// 이 밑에가 바뀐거 적용한거임 위에거는 나중에 지우기
data class PlaceSearchData(
    val placeId: String,
    val placeTitle: String,
    val placeAddress: String,
)

data class PlaceDetailData(
    val placeId: String,
    val placeTitle: String,
    val placeAddress: String,
//    val placeType: String,
    val placeImgsUrl: ArrayList<String>,
    val menus: ArrayList<PlaceDetailMenuData>,
    val timeInfo: String,
    val latitude: String,
    val longitude: String,
)

data class PlaceDetailMenuData(
    val menuTitle: String,
    val menuPrice: String,
)
