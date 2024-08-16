package com.example.ourmenu.landing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmenu.account.AccountActivity
import com.example.ourmenu.databinding.ActivityLandingBinding

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
            finish()
        }
    }
}
