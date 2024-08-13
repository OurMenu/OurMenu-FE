package com.example.ourmenu.data.menuFolder.data

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
