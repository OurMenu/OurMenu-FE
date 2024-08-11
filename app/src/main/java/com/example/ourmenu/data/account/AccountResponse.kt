package com.example.ourmenu.data.account

import com.example.ourmenu.data.ErrorResponse

data class AccountResponse (
    val isSuccess : Boolean,
    val response: AccountResponseData,
    val errorResponse: ErrorResponse
)

data class AccountEmailResponse(
    val isSuccess : Boolean,
    val response: AccountEmailCodeData,
    val errorResponse: ErrorResponse
)
