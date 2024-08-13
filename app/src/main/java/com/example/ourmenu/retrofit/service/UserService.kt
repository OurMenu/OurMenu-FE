package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.user.UserImageData
import com.example.ourmenu.data.user.UserNicknameData
import com.example.ourmenu.data.user.UserPasswordData
import com.example.ourmenu.data.user.UserPatchResponse
import com.example.ourmenu.data.user.UserResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part

interface UserService {
    @PATCH("user/password")
    fun patchUserPassword(
        @Body body: UserPasswordData
    ): Call<UserPatchResponse>

    @PATCH("user/nickname")
    fun patchUserNickname(
        @Body nickname: UserNicknameData
    ): Call<UserPatchResponse>
    @Multipart
    @PATCH("user/image")
    fun patchUserImage(
        @Part imgFile : MultipartBody.Part
    ): Call<UserPatchResponse>

    @GET("user")
    fun getUser() : Call<UserResponse>
}
