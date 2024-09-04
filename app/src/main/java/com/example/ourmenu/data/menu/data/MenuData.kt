package com.example.ourmenu.data.menu.data

data class MenuData(
    val groupId: Int,
    val menuId: Int,
    val menuTitle: String,
    val placeTitle: String,
    val placeAddress: String,
    val menuPrice: Int,
    val menuImgUrl: String,
    val createdAt: String,
)

data class MenuFolder(
    val menuFolderIcon: String,
    val menuFolderTitle: String,
    val menuFolderCount: Int,
)

data class MenuPlaceDetailData(
    val groupId: Int,
    val menuTitle: String,
    val menuPrice: Int,
    val menuIconType: String,
    val placeTitle: String,
    val latitude: Double,
    val longitude: Double,
    val menuTags: ArrayList<MenuTag>,
    val menuImgsUrl: ArrayList<MenuImgUrl>,
    val menuFolder: MenuFolder,
)

data class MenuImgUrl(
    val menuImgUrl: String? = "",
)

data class MenuInfoData(
    val groupId: Int,
    val menuTitle: String,
    val menuPrice: Int,
    val menuMemoTitle: String,
    val menuMemo: String,
    val menuIconType: String,
    val menuPlaceInfo: MenuPlaceInfoData,
    val menuTags: ArrayList<MenuTag>,
    val menuImages: ArrayList<MenuImgUrl>,
    val menuFolders: ArrayList<MenuFolderChip>,
)

data class MenuPlaceInfoData(
    val placeTitle: String,
    val placeAddress: String,
    val placeInfo: String,
)

data class MenuTag(
    val tagTitle: String,
    val custom: Boolean,
)

data class MenuFolderChip(
    val menuFolderTitle: String,
    val menuFolderIcon: String,
)
