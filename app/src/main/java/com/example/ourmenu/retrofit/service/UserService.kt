package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.community.StrResponse
import com.example.ourmenu.data.user.PasswordResponse
import com.example.ourmenu.data.user.UserNicknameData
import com.example.ourmenu.data.user.UserPasswordData
import com.example.ourmenu.data.user.UserPatchResponse
import com.example.ourmenu.data.user.UserResponse
import com.example.ourmenu.data.user.email
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
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
        @Part imgFile: MultipartBody.Part?
    ): Call<UserPatchResponse>

    @GET("user")
    fun getUser() : Call<UserResponse>

    @POST("user/temporaryPassword")
    fun postTemporaryPassword(
        @Body email : email
    ):Call<PasswordResponse>
}
