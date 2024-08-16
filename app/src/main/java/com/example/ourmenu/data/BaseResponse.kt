package com.example.ourmenu.data

// /menuFolder/priority/{menuFolderId} PATCH
data class BaseResponse(
    val isSuccess: Boolean,
    val response: String
)

data class BaseResponseWithError(
    val isSuccess: Boolean,
    val response: String,
    val errorResponse: ErrorResponse
)
