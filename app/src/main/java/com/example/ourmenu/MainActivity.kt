package com.example.ourmenu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmenu.community.CommunityFragment
import com.example.ourmenu.databinding.ActivityMainBinding
import com.example.ourmenu.home.HomeFragment
import com.example.ourmenu.map.MapFragment
import com.example.ourmenu.menu.MenuFragment
import com.example.ourmenu.mypage.MypageFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        initBottomNavigation()

        setContentView(binding.root)
    }

    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, MapFragment())
            .commitAllowingStateLoss()
        binding.mainBottomNav.selectedItemId = R.id.home_fragment

        binding.mainBottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.map_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, MapFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.menu_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, MenuFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.community_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, CommunityFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.mypage_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, MypageFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

    }
}
