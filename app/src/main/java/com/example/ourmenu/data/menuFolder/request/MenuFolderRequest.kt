package com.example.ourmenu.data.menuFolder.request

import android.net.Uri
import com.google.gson.annotations.SerializedName

// /menuFolder/{menuFolderId}
data class MenuFolderRequest(
    val menuFolderTitle: String,
    val menuFolderImg: Uri? = null,
    val menuFolderIcon: String,
    val menuIds: ArrayList<Int>? = null
)

data class MenuFolderPatchRequest(
    val menuFolderTitle: String,
    val menuFolderImg: Uri? = null,
    val menuFolderIcon: String,
    val menuIds: ArrayList<Int>? = null
)

