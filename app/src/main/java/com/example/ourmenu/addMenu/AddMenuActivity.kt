package com.example.ourmenu.addMenu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmenu.R
import com.example.ourmenu.databinding.ActivityAddMenuBinding
import com.example.ourmenu.retrofit.NetworkModule

class AddMenuActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkModule.initialize(applicationContext)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.cl_add_menu_main, AddMenuMapFragment())
                .commitNow()
        }
    }
}
