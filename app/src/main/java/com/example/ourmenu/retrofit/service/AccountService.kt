package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.account.AccountEmailData
import com.example.ourmenu.data.account.AccountEmailResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountService {

    @POST("account/email")
    fun getAccountEmail(
        @Body email: AccountEmailData,
    ): Call<AccountEmailResponse>
}
