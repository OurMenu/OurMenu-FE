package com.example.ourmenu.data.community

import com.example.ourmenu.data.ErrorResponse

data class ArticleResponse(
    val isSuccess : Boolean,
    val response: ArticleResponseData,
    val errorResponse: ErrorResponse
)

data class CommunityResponse(
    val isSuccess : Boolean,
    val response: ArrayList<CommunityResponseData>,
    val errorResponse: ErrorResponse
)
