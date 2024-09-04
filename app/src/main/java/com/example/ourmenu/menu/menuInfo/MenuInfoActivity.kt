package com.example.ourmenu.menu.menuInfo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.ourmenu.R
import com.example.ourmenu.databinding.ActivityMenuInfoBinding
import com.example.ourmenu.retrofit.NetworkModule

class MenuInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityMenuInfoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuInfoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        NetworkModule.initialize(applicationContext)

        val menuInfoFragment = MenuInfoFragment()
        val menuInfoMapFragment = MenuInfoMapFragment()

        val tag = intent.getStringExtra("tag")
        when (tag) {
            "menuInfo" -> {
                setMenuInfoData(menuInfoFragment)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.menu_info_frm, menuInfoFragment)
                    .commitAllowingStateLoss()
            }

            "menuInfoMap" -> {
                setMenuInfoData(menuInfoMapFragment)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.menu_info_frm, menuInfoMapFragment)
                    .commitAllowingStateLoss()
            }

            else -> {
                Log.d("tag", "NO-TAG")
            }
        }
    }

    private fun setMenuInfoData(fragment: Fragment) {
        val groupId = intent.getIntExtra("groupId", -1)
        val bundle = bundleOf("groupId" to groupId)

        fragment.arguments = bundle

    }
}
