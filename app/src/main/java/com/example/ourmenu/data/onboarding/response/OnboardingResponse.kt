package com.example.ourmenu.data.onboarding.response


import com.example.ourmenu.data.onboarding.data.OnboardingData
import com.example.ourmenu.data.onboarding.data.OnboardingRecommendData
import com.example.ourmenu.data.onboarding.data.OnboardingTagData
import com.google.gson.annotations.SerializedName

// onboarding
data class OnboardingResponse(
    val isSuccess: Boolean,
    @SerializedName("response")
    val response: ArrayList<OnboardingData>
)

// onboarding/recommend
data class OnboardingRecommendResponse(
    val isSuccess: Boolean,
    @SerializedName("response")
    val response: OnboardingRecommendData
)

// onboarding/recommend/tag
data class OnboardingTagResponse(
    val isSuccess: Boolean,
    val response: ArrayList<OnboardingTagData>
)
