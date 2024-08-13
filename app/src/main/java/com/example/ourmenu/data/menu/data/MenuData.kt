package com.example.ourmenu.data.menu.data

data class MenuData(
    val menuId: Int,
    val groupId: Int,
    val menuTitle: String,
    val placeTitle: String,
    val placeAddress: String,
    val menuPrice: Int,
    val menuImgUrl: String,
)

data class MenuDetailData(
    val groupId: Int,
    val menuFolders: List<MenuFolder>,
    val menuIconType: String,
    val menuImages: List<MenuImage>,
    val menuMemo: String,
    val menuMemoTitle: String,
    val menuPrice: Int,
    val menuTags: List<MenuTag>,
    val menuTitle: String,
)

data class MenuFolder(
    val menuFolderIcon: String,
    val menuFolderTitle: String,
    val menuFolderCount: Int,
)

data class MenuImage(
    val menuImgUrl: String,
)

data class MenuTag(
    val tagTitle: String,
    val custom: Boolean,
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
    val menuImgsUrl: ArrayList<MenuImgsUrl>,
    val menuFolder: MenuFolder,
)

data class MenuImgsUrl(
    val menuImgUrl: String,
)
