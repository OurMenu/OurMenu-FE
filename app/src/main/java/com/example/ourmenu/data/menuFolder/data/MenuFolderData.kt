package com.example.ourmenu.data.menuFolder.data

import com.google.gson.annotations.SerializedName


// /menuFolder
data class MenuFolderData(
    val menuFolderId: Int,
    val menuFolderTitle: String,
    val menuCount: Int,
    val menuFolderImgUrl: String? = "",
    val menuFolderIcon: String,
    val menuFolderPriority: Int,
    val menuIds: ArrayList<Int> // [ menuId, groupId ]
)

