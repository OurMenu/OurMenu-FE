package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.menu.response.MenuArrayResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MenuService {
    @GET("menu")
    fun getMenus(
        @Query("menuTitle") menuTitle: String,
        @Query("menuTag") menuTag: ArrayList<String>,
        @Query("menuFolderId") menuFolderId: Int,
    ): Call<MenuArrayResponse>
}
