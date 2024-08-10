package com.example.ourmenu.data.onboarding.data


import com.google.gson.annotations.SerializedName

data class OnboardingData(
    val questionId: Int,
    val question: String,
    val yes: String,
    val yesImg: String,
    val yesAnswerUrl: String,
    val no: String,
    val noImg: String,
    val noAnswerUrl: String
)

data class OnboardingRecommendData(
    @SerializedName("recommandImgUrl")
    val recommendImgUrl: String,
    val menus: ArrayList<OnboardingMenuData>
)

data class OnboardingMenuData(
    val groupId: Int,
    val menuImgUrl: String,
    val menuTitle: String,
    val placeName: String
)
