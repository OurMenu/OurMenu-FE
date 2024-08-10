package com.example.ourmenu.retrofit.service

import com.example.ourmenu.data.onboarding.response.OnboardingResponse
import retrofit2.Call
import retrofit2.http.GET

interface OnboardingService {
    @GET("onboarding")
    fun getOnboarding(): Call<OnboardingResponse>
}
