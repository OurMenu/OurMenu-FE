package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.menu.request.MenuRequest
import com.example.ourmenu.data.menu.response.MenuArrayResponse
import com.example.ourmenu.data.menu.response.MenuInfoResponse
import com.example.ourmenu.data.menu.response.MenuPlaceDetailResponse
import com.example.ourmenu.data.menu.response.PostMenuPhotoResponse
import com.example.ourmenu.data.menu.response.PostMenuResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MenuService {
    @GET("menu")
    fun getMenus(
        @Query("tags") tags: ArrayList<String>?, // 태그 (예: "tag1,tag2")
        @Query("title") title: String?, // 제목
        @Query("menuFolderId") menuFolderId: Int?, // 메뉴 폴더 ID
        @Query("page") page: Int?, // 페이지 번호
        @Query("size") size: Int?, // 페이지 크기
        @Query("minPrice") minPrice: Int, // 최소 가격
        @Query("maxPrice") maxPrice: Int, // 최대 가격
    ): Call<MenuArrayResponse>

    @POST("menu")
    fun postMenu(
        @Body body: MenuRequest,
    ): Call<PostMenuResponse>

    @Multipart
    @POST("menu/photo")
    fun postMenuPhoto(
        @Part menuImgs: ArrayList<MultipartBody.Part?>,
        @Part("menuGroupId") menuGroupId: RequestBody,
    ): Call<PostMenuPhotoResponse>

    @GET("menu/place/{placeId}")
    fun getMenuPlaceDetail(
        @Path("placeId") placeId: Int,
    ): Call<MenuPlaceDetailResponse>

    @GET("menu/{groupId}")
    fun getMenuInfo(
        @Path("groupId") groupId: Int,
    ): Call<MenuInfoResponse>
}
