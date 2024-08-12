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

data class MapInfoDetailData(
    val groupId: Int,
    val latitude: Double,
    val longitude: Double,
    val menuFolder: MenuFolder,
    val menuIconType: String,
    val menuImgsUrl: ArrayList<MenuImgsUrl>,
    val menuPrice: Int,
    val menuTags: ArrayList<MenuTag>,
    val menuTitle: String,
    val placeTitle: String,
)

data class MenuFolder(
    val menuFolderCount: Int,
    val menuFolderIcon: String,
    val menuFolderTitle: String,
)

data class MenuImgsUrl(
    val menuImgUrl: String,
)

data class MenuTag(
    val custom: Boolean,
    val tagTitle: String,
)
