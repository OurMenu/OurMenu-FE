package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.BaseResponse
import com.example.ourmenu.data.menuFolder.request.MenuFolderPatchRequest
import com.example.ourmenu.data.menuFolder.request.MenuFolderRequest
import com.example.ourmenu.data.menuFolder.response.GetMenuFolderResponse
import com.example.ourmenu.data.menuFolder.response.MenuFolderResponse
import com.example.ourmenu.data.menuFolder.response.MenuFolderArrayResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MenuFolderService {
    @GET("menuFolder")
    fun getMenuFolders(): Call<MenuFolderArrayResponse>

    @POST("menuFolder")
    @Multipart
    fun postMenuFolder(
//        @Body menuFolderRequest: MenuFolderRequest
        @Part menuFolderImage: MultipartBody.Part?,
        @Part("menuFolderTitle") menuFolderTitle: RequestBody,
        @Part("menuFolderIcon") menuFolderIcon: RequestBody,
        @Part("menuIds") menuIds: ArrayList<Int>
    ): Call<MenuFolderResponse>

    @PATCH("menuFolder/{menuFolderId}")
    @Multipart
    fun patchMenuFolder(
        @Path("menuFolderId") menuFolderId: Int,
        @Part menuFolderImage: MultipartBody.Part?,
        @Part("menuFolderTitle") menuFolderTitle: RequestBody,
        @Part("menuFolderIcon") menuFolderIcon: RequestBody,
    ): Call<MenuFolderResponse>

    @DELETE("menuFolder/{menuFolderId}")
    fun deleteMenuFolder(
        @Path("menuFolderId") menuFolderId: Int
    ): Call<BaseResponse>

    @PATCH("/menuFolder/priority/{menuFolderId}")
    fun patchPriority(
        @Path("menuFolderId") menuFolderId: Int,
        @Query("newPriority") newPriority: Int
    ): Call<BaseResponse>

    @GET("/menuFolder/{menuFolderId}")
    fun getMenuFolder(
        @Path("menuFolderId") menuFolderId: Int
    ): Call<GetMenuFolderResponse>
}
