package com.example.ourmenu.data.menu.request

data class MenuRequest(
    val menuFolderIds: ArrayList<Int>,
    val menuIcon: String,
    val menuMemo: String,
    val menuPrice: Int,
    val menuTitle: String,
    val storeInfo: StoreInfo,
    val tagInfo: ArrayList<TagInfo>,
)

data class StoreInfo(
    val storeAddress: String,
    val storeLatitude: Double,
    val storeLongitude: Double,
    val storeMemo: String, // TODO: 이게 가게 운영시간 역할인지 확인
    val storeName: String,
)

data class TagInfo(
    val isCustom: Boolean,
    val tagTitle: String,
)
