package com.example.ourmenu.data.menuFolder.data

import com.example.ourmenu.data.menu.data.MenuData

// /menuFolder
data class MenuFolderData(
    val menuFolderId: Int,
    val menuFolderTitle: String,
    val menuCount: Int,
    val menuFolderImgUrl: String? = "",
    val menuFolderIcon: String,
    val menuFolderPriority: Int,
    val menuIds: ArrayList<MenuIdsData>,
)

data class MenuIdsData(
    val menuId: Int,
    val groupId: Int,
)

data class GetMenuFolderData(
    val menuFolderId: Int,
    val menuFolderTitle: String,
    val menuCount: Int,
    val menuFolderImg: String? = "",
    val menuFolderIcon: String,
    val menus: ArrayList<MenuData>,
)

