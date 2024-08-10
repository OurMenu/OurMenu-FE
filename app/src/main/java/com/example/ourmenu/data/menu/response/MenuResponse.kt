package com.example.ourmenu.data.menu.response

import com.example.ourmenu.data.menu.data.MenuData

// /menu
data class MenuArrayResponse(
    val isSuccess: Boolean,
    val response: ArrayList<MenuData>
)

