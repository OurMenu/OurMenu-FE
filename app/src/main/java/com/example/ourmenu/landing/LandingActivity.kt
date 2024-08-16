package com.example.ourmenu.landing

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmenu.MainActivity
import com.example.ourmenu.account.AccountActivity
import com.example.ourmenu.data.account.AccountLoginData
import com.example.ourmenu.data.account.AccountResponse
import com.example.ourmenu.databinding.ActivityLandingBinding
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.AccountService
import retrofit2.Call
import retrofit2.Response

class LandingActivity : AppCompatActivity() {
    lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLandingLogin.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra("fragment", "login")
            startActivity(intent)
            finish()
        }

        binding.btnLandingSignup.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra("fragment", "signup")
            startActivity(intent)
        }
    }
}
