package com.example.ourmenu.data.menuFolder.response

import com.example.ourmenu.data.menuFolder.data.MenuFolderData

// /menuFolder GET
data class MenuFolderArrayResponse(
    val isSuccess: Boolean,
    val response: ArrayList<MenuFolderData>,
)

// /menuFolder/{menuFolderId} PATCH
data class MenuFolderResponse(
    val isSuccess: Boolean,
    val response: MenuFolderData
)



