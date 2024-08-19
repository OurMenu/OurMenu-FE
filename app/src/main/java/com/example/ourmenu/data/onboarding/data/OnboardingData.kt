package com.example.ourmenu.data.onboarding.data

import com.google.gson.annotations.SerializedName

data class OnboardingData(
    val questionId: Int,
    val question: String,
    val yes: String,
    val yesImg: Int,
    val no: String,
    val noImg: Int,
)

data class OnboardingRecommendData(
    @SerializedName("recommendImgUrl")
    val recommendImgUrl: String,
    val menus: ArrayList<OnboardingMenuData>,
)

data class OnboardingTagData(
    val tagName: String,
    val menus: ArrayList<OnboardingMenuData>,
)

data class OnboardingMenuData(
    val menuImgUrl: String,
    val menuTitle: String,
    val placeName: String,
    val groupId: Int,
    val userOwned: Boolean
)

data class OnboardingStateData(
    val questionId: Int,
    val answerType: String,
)
