package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.account.AccountConfirmCodeData
import com.example.ourmenu.data.account.AccountEmailCodeData
import com.example.ourmenu.data.account.AccountEmailData
import com.example.ourmenu.data.account.AccountEmailResponse
import com.example.ourmenu.data.account.AccountLoginData
import com.example.ourmenu.data.account.AccountRefreshTokenData
import com.example.ourmenu.data.account.AccountResponse
import com.example.ourmenu.data.account.AccountSignupData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountService {

    @POST("account/email")
    fun postAccountEmail(
        @Body email: AccountEmailData,
    ): Call<AccountEmailResponse>

    @POST("account/confirmCode")
    fun postAccountCode(
        @Body body: AccountConfirmCodeData
    ): Call<AccountResponse>

    @POST("account/signup")
    fun postAccountSignup(
        @Body body : AccountSignupData
    ): Call<AccountResponse>

    @POST("account/reissueToken")
    fun postAccountReissue(
        @Body refreshToken : AccountRefreshTokenData
    ): Call<AccountResponse>

    @POST("account/login")
    fun postAccountLogin(
        @Body body: AccountLoginData
    ):Call<AccountResponse>

    @POST("account/logout")
    fun postAccountLogout() : Call<AccountResponse>
}
