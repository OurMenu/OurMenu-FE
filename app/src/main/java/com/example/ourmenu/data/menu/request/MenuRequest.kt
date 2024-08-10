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
    val storeLatitude: Int,
    val storeLongitude: Int,
    val storeMemo: String,
    val storeName: String,
)

data class TagInfo(
    val isCustom: Boolean,
    val tagTitle: String,
)
