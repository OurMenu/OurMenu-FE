package com.example.ourmenu.data.onboarding.response


import com.example.ourmenu.data.onboarding.data.OnboardingData
import com.google.gson.annotations.SerializedName

data class OnboardingResponse(
    val isSuccess: Boolean,
    @SerializedName("response")
    val onBoardingData: ArrayList<OnboardingData>
)
