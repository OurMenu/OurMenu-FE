package com.example.ourmenu.menu.menuInfo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmenu.R
import com.example.ourmenu.databinding.ActivityMenuInfoBinding

class MenuInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityMenuInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuInfoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val tag = intent.getStringExtra("tag")
        when (tag) {
            "menuInfo" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.menu_info_frm, MenuInfoFragment())
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
}
