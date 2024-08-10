package com.example.ourmenu.data.account

import java.io.Serializable

data class AccountResponseData(
    val grantType : String,
    val accessToken : String,
    val refreshToken : String,
    val accessTokenExpiredAt : String,
    val refreshTokenExpiredAt: String
)

//account/signup
data class AccountSignupData(
    val email: String,
    val password : String,
    val nickname : String
)

//account/reissueToken
data class AccountReissueTokenData(
    val refreshToken: String
)

//account/login
data class AccountLoginData(
    val email : String,
    val password: String
)

//account/email
data class AccountEmailData(
    val email:String
):Serializable

data class AccountEmailCodeData(
    val code : String
):Serializable


//account/confirmCode
data class AccountConfirmCodeData(
    val email : String,
    val code: String
)

