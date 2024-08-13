package com.example.ourmenu.menu.menuInfo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.ourmenu.R
import com.example.ourmenu.databinding.ActivityMenuInfoBinding

class MenuInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityMenuInfoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuInfoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val menuInfoFragment = MenuInfoFragment()
        setMenuInfoData(menuInfoFragment)

        val tag = intent.getStringExtra("tag")
        when (tag) {
            "menuInfo" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.menu_info_frm, menuInfoFragment)
                    .commitAllowingStateLoss()
            }

            "menuInfoMap" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.menu_info_frm, MenuInfoMapFragment())
                    .commitAllowingStateLoss()
            }

            else -> {
                Log.d("tag", "NO-TAG")
            }
        }
    }

    private fun setMenuInfoData(menuInfoFragment: MenuInfoFragment) {
        val groupId = intent.getIntExtra("groupId", -1)
        val bundle = bundleOf("groupId" to groupId)

        menuInfoFragment.arguments = bundle

    }
}
