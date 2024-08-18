package com.example.ourmenu.home.recommend

import android.util.Log
import com.example.ourmenu.R
import com.example.ourmenu.data.onboarding.data.OnboardingData
import com.example.ourmenu.databinding.FragmentHomeBinding
import kotlin.random.Random

class RecommendMain {
    companion object {
        val onBoardingList =
            arrayListOf(
                OnboardingData(
                    questionId = 1,
                    question = "오늘 기분은 어떠신가요",
                    yes = "좋아!",
                    yesImg = R.drawable.ic_1_yes,
                    no = "별로야..",
                    noImg = R.drawable.ic_1_no,
                ),
                OnboardingData(
                    questionId = 2,
                    question = "오늘 날씨는 어떤가요?",
                    yes = "맑아",
                    yesImg = R.drawable.ic_2_yes,
                    no = "비가 오네",
                    noImg = R.drawable.ic_2_no,
                ),
                OnboardingData(
                    questionId = 3,
                    question = "스트레스 받을 때는 어떤 음식을 드시나요?",
                    yes = "달달한 음식을 먹어",
                    yesImg = R.drawable.ic_3_yes,
                    no = "매운 음식이지!",
                    noImg = R.drawable.ic_3_no,
                ),
                OnboardingData(
                    questionId = 4,
                    question = "어디로 떠나고 싶은가요?",
                    yes = "바다",
                    yesImg = R.drawable.ic_4_yes,
                    no = "산",
                    noImg = R.drawable.ic_4_no,
                ),
                OnboardingData(
                    questionId = 5,
                    question = "어느 계절을 더 좋아하세요?",
                    yes = "여름",
                    yesImg = R.drawable.ic_5_yes,
                    no = "겨울",
                    noImg = R.drawable.ic_5_no,
                ),
            )

        fun setRecommendMain(
            questionId: Int,
            answerType: String,
            binding: FragmentHomeBinding,
        ) {
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
    companion object {
        fun setRecommendTag(
            tagFirst: String,
            tagSecond: String,
            binding: FragmentHomeBinding,
        ) {
            when (tagFirst) {
                "집밥이 그리울 땐," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_soban)
                }

                "짜장 먹을까? 짬뽕 먹을까?" -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_jjajangmyeon)
                }

                "일본 여행이 가고 싶을 땐," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_sushi)
                }

                "기분 내고 싶은 오늘은," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_gibun)
                }

                "독특한 향을 느끼고 싶을 땐," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_leaf)
                }

                "밥이 먹고 싶을 땐," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_rice)
                }

                "빵이 먹고 싶을 땐," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_bread)
                }

                "면이 먹고 싶을 땐," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_ramen)
                }

                "고기 구우러 가고 싶을 땐," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_meat)
                }

                "부드러운 속살의 고소한 생선이 떠오를 땐," -> {
                    binding.tvHomeTagSubFirst.text = "고소한 생선이 떠오를 땐,"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_fish)
                }

                "달달한 디저트가 땡길 땐," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_donut)
                }

                "커피가 생각날 땐," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_coffee)
                }

                "빠르고 맛있게!" -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_hamburger)
                }

                "스트레스 풀리는 매콤함," -> {
                    binding.tvHomeTagSubFirst.text = "스트레스가 확 풀리는 매콤함"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_fire)
                }

                "기분 좋아지는 달달함," -> {
                    binding.tvHomeTagSubFirst.text = "기분이 좋아지는 달달함"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_donut)
                }

                "더위가 사라지는 시원함," -> {
                    binding.tvHomeTagSubFirst.text = "더위가 사라지는 시원함"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_ice_cream)
                }

                "땀나는 뜨끈함," -> {
                    binding.tvHomeTagSubFirst.text = "땀이 뻘뻘 나는 뜨끈함"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_samgyetang)
                }

                "얼큰함이 살아있는, " -> {
                    binding.tvHomeTagSubFirst.text = "얼큰함이 살아있는,"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_soup_hot)
                }

                "혼자 밥먹기 좋은 곳," -> {
                    binding.tvHomeTagSubFirst.text = "혼자 밥먹기 좋은 곳"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_alone)
                }

                "비즈니스미팅이 있을 땐," -> {
                    binding.tvHomeTagSubFirst.text = "비즈니스 미팅이 있을 땐,"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_business)
                }

                "친구와 약속이 있다면?" -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_picnic)
                }

                "데이트 하는 날엔," -> {
                    binding.tvHomeTagSubFirst.text = "데이트를 하는 날엔,"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_heart)
                }

                "밥약하기 좋은 곳," -> {
                    binding.tvHomeTagSubFirst.text = "밥약하기 좋은 곳,"
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_spoon_chopstick)
                }

                "단체로 방문한다면," -> {
                    binding.tvHomeTagSubFirst.text = tagFirst
                    binding.ivHomeIconTagSubFirst.setImageResource(R.drawable.ic_rec_tag_group)
                }

                else -> {
                    Log.d("ts", tagFirst)
                }
            }

            when (tagSecond) {
                "집밥이 그리울 땐," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "짜장 먹을까? 짬뽕 먹을까?" -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "일본 여행이 가고 싶을 땐," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "기분 내고 싶은 오늘은," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_gibun)
                }

                "독특한 향을 느끼고 싶을 땐," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_leaf)
                }

                "밥이 먹고 싶을 땐," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "빵이 먹고 싶을 땐," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "면이 먹고 싶을 땐," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "고기 구우러 가고 싶을 땐," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_meat)
                }

                "부드러운 속살의 고소한 생선이 떠오를 땐," -> {
                    binding.tvHomeTagSubSecond.text = "고소한 생선이 떠오를 땐,"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_fish)
                }

                "달달한 디저트가 땡길 땐," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_donut)
                }

                "커피가 생각날 땐," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "빠르고 맛있게!" -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_hamburger)
                }

                "스트레스 풀리는 매콤함," -> {
                    binding.tvHomeTagSubSecond.text = "스트레스가 확 풀리는 매콤함"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_fire)
                }

                "기분 좋아지는 달달함," -> {
                    binding.tvHomeTagSubSecond.text = "기분이 좋아지는 달달함"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "더위가 사라지는 시원함," -> {
                    binding.tvHomeTagSubSecond.text = "더위가 사라지는 시원함"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_ice_cream)
                }

                "땀나는 뜨끈함," -> {
                    binding.tvHomeTagSubSecond.text = "땀이 뻘뻘 나는 뜨끈함"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_samgyetang)
                }

                "얼큰함이 살아있는, " -> {
                    binding.tvHomeTagSubSecond.text = "얼큰함이 살아있는,"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_soup_hot)
                }

                "혼자 밥먹기 좋은 곳," -> {
                    binding.tvHomeTagSubSecond.text = "혼자 밥먹기 좋은 곳"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "비즈니스미팅이 있을 땐," -> {
                    binding.tvHomeTagSubSecond.text = "비즈니스 미팅이 있을 땐,"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "친구와 약속이 있다면?" -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_picnic)
                }

                "데이트 하는 날엔," -> {
                    binding.tvHomeTagSubSecond.text = "데이트를 하는 날엔,"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "밥약하기 좋은 곳," -> {
                    binding.tvHomeTagSubSecond.text = "밥약하기 좋은 곳,"
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                "단체로 방문한다면," -> {
                    binding.tvHomeTagSubSecond.text = tagSecond
                    binding.ivHomeIconTagSubSecond.setImageResource(R.drawable.ic_rec_tag_sun)
                }

                else -> {
                    Log.d("ts", tagSecond)
                }
            }
        }
    }
}
