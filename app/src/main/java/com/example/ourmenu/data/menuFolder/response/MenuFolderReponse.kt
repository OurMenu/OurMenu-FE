package com.example.ourmenu.data.menuFolder.response

import com.example.ourmenu.data.menuFolder.data.GetMenuFolderData
import com.example.ourmenu.data.menuFolder.data.MenuFolderData

// /menuFolder GET
data class MenuFolderArrayResponse(
    val isSuccess: Boolean,
    val response: MenuFolderResponseWithAllCount
)

// /menuFolder GET response
data class MenuFolderResponseWithAllCount(
    val menuCount: Int,
    val menuFolders: ArrayList<MenuFolderData>
)

// /menuFolder/{menuFolderId} PATCH
data class MenuFolderResponse(
    val isSuccess: Boolean,
    val response: MenuFolderData
)

// /menuFolder/{menuFolderId} PATCH
data class GetMenuFolderResponse(
    val isSuccess: Boolean,
    val response: GetMenuFolderData
)




