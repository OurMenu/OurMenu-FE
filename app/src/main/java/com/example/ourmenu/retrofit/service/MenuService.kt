package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.menu.request.MenuRequest
import com.example.ourmenu.data.menu.response.MenuArrayResponse
import com.example.ourmenu.data.menu.response.PostMenuResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MenuService {
    @GET("menu")
    fun getMenus(
        @Query("menuTitle") menuTitle: String,
        @Query("menuTag") menuTag: ArrayList<String>,
        @Query("menuFolderId") menuFolderId: Int,
    ): Call<MenuArrayResponse>

    @POST("menu")
    fun postMenu(
        @Body body: MenuRequest,
    ): Call<PostMenuResponse>

    @POST("menu/photo")
    fun postMenuPhoto(
        @Part menuImgs: ArrayList<MultipartBody.Part?>,
        @Part("menuGroupId") menuGroupId: RequestBody,
    )
}
