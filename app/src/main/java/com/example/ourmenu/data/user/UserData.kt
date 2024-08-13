package com.example.ourmenu.data.user

import retrofit2.http.Multipart

data class UserPasswordData(
    val password : String,
    val newPassword : String
)
data class UserNicknameData(
    val nickname: String
)
data class UserImageData(
    val imgFile: String
)
data class UserData(
    val userId : Int,
    val email : String,
    val nickname : String,
    val imageUrl : String
)
