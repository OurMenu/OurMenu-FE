package com.example.ourmenu.home.recommend

import com.example.ourmenu.R
import com.example.ourmenu.data.onboarding.data.OnboardingData
import com.example.ourmenu.databinding.FragmentHomeBinding
import kotlin.random.Random

class RecommendMain {

    companion object {
        val onBoardingList = arrayListOf(
            OnboardingData(
                questionId = 1,
                question = "오늘 기분은 어떠신가요",
                yes = "좋아!",
                yesImg = R.drawable.img_1_yes,
                no = "별로야..",
                noImg = R.drawable.img_1_no,
            ),
            OnboardingData(
                questionId = 2,
                question = "오늘 날씨는 어떤가요?",
                yes = "맑아",
                yesImg = R.drawable.img_2_yes,
                no = "비가 오네",
                noImg = R.drawable.img_2_no
            ),
            OnboardingData(
                questionId = 3,
                question = "스트레스 받을 때는 어떤 음식을 드시나요?",
                yes = "달달한 음식을 먹어",
                yesImg = R.drawable.img_3_yes,
                no = "매운 음식이지!",
                noImg = R.drawable.img_3_no
            ),
            OnboardingData(
                questionId = 4,
                question = "어디로 떠나고 싶은가요?",
                yes = "바다",
                yesImg = R.drawable.img_4_yes,
                no = "산",
                noImg = R.drawable.img_4_no
            ),
            OnboardingData(
                questionId = 5,
                question = "어느 계절을 더 좋아하세요?",
                yes = "여름",
                yesImg = R.drawable.img_5_yes,
                no = "겨울",
                noImg = R.drawable.img_5_no
            )
        )


        fun setRecommendMain(questionId: Int, answerType: String, binding: FragmentHomeBinding) {
            val answerYes = if (answerType == "YES") true else false
            when (questionId) {
                1 -> {
                    if (answerYes) {
                        val randomValue = Random.nextInt(2)
                        if (randomValue == 1) {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_1_yes_1)
                        } else {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_1_yes_2)
                        }
                    } else {
                        val randomValue = Random.nextInt(2)
                        if (randomValue == 1) {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_1_no_1)
                        } else {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_1_no_2)
                        }
                    }
                }

                2 -> {
                    if (answerYes) {
                        binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_2_yes_1)
                    } else {
                        binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_2_no_1)
                    }
                }

                3 -> {
                    if (answerYes) {
                        val randomValue = Random.nextInt(2)
                        if (randomValue == 1) {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_3_yes_1)
                        } else {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_3_yes_2)
                        }
                    } else {
                        binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_3_no_1)
                    }
                }

                4 -> {
                    if (answerYes) {
                        val randomValue = Random.nextInt(2)
                        if (randomValue == 1) {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_4_yes_1)
                        } else {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_4_yes_2)
                        }
                    } else {
                        binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_4_no_1)
                    }
                }

                5 -> {
                    if (answerYes) {
                        val randomValue = Random.nextInt(2)
                        if (randomValue == 1) {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_5_yes_1)
                        } else if (randomValue == 2) {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_5_yes_2)
                        } else {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_5_yes_3)
                        }
                    } else {
                        val randomValue = Random.nextInt(2)
                        if (randomValue == 1) {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_5_no_1)
                        } else {
                            binding.ivHomeRecommendMessage.setImageResource(R.drawable.img_recommend_5_no_2)
                        }
                    }
                }
            }
        }

    }
}

class RecommendTag {

}
