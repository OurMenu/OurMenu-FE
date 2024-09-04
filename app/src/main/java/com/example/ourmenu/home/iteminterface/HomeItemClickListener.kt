package com.example.ourmenu.home.iteminterface

import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.data.onboarding.data.OnboardingMenuData
import com.example.ourmenu.databinding.ItemHomeMenuMainBinding
import com.example.ourmenu.databinding.ItemHomeMenuSubBinding

interface HomeItemClickListener {
    fun onItemClick(onboardingMenuData: OnboardingMenuData)
}
