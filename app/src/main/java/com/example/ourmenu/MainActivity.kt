package com.example.ourmenu

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ourmenu.community.CommunityFragment
import com.example.ourmenu.databinding.ActivityMainBinding
import com.example.ourmenu.home.HomeFragment
import com.example.ourmenu.map.MapFragment
import com.example.ourmenu.menu.menuFolder.MenuFolderFragment
import com.example.ourmenu.mypage.MypageFragment
import com.example.ourmenu.retrofit.NetworkModule

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkModule.initialize(this)
        onBackPressedDispatcher()

        initBottomNavigation()

        // 처음 화면을 HomeFragment로 설정
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }
    }

    private fun onBackPressedDispatcher() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                if (System.currentTimeMillis() - backPressedTime <= 2000) {
                    finishAffinity()
                } else {
                    backPressedTime = System.currentTimeMillis()
                }
            }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun initBottomNavigation() {
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.main_frm, HomeFragment())
//            .commitAllowingStateLoss()
        binding.mainBottomNav.selectedItemId = R.id.home_fragment

        binding.mainBottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_fragment -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.map_fragment -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_frm, MapFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.menu_fragment -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_frm, MenuFolderFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.community_fragment -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_frm, CommunityFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.mypage_fragment -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_frm, MypageFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}
