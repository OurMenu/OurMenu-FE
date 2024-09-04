package com.example.ourmenu.data.user

import com.example.ourmenu.data.ErrorResponse

data class UserPatchResponse(
    val isSuccess : Boolean,
    val errorResponse: ErrorResponse
)
data class UserResponse(
    val isSuccess : Boolean,
    val response: UserData,
    val errorResponse: ErrorResponse
)

data class PasswordResponse(
    val isSuccess : Boolean,
    val response: password,
    val errorResponse: ErrorResponse
)
