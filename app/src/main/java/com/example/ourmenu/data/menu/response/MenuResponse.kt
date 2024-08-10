package com.example.ourmenu.data.menu.response

import com.example.ourmenu.data.ErrorResponse
import com.example.ourmenu.data.menu.data.MenuData

// /menu
data class MenuArrayResponse(
    val isSuccess: Boolean,
    val response: ArrayList<MenuData>,
)

// menu (post)
data class PostMenuResponse(
    val errorResponse: ErrorResponse,
    val isSuccess: Boolean,
    val response: MenuGroupId,
)

data class MenuGroupId(
    val menuGroupId: Int,
)
