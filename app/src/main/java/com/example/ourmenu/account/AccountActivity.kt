package com.example.ourmenu.account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ourmenu.R
import com.example.ourmenu.databinding.ActivityAccountBinding

class AccountActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentType = intent.getStringExtra("fragment")

        if (fragmentType == "login") {
            replaceFragment(LoginFragment())
        } else if (fragmentType == "signup") {
            replaceFragment(SignupEmailFragment())
        }

        binding.ivBack.setOnClickListener {
            if (supportFragmentManager.backStackEntryCount == 0){
                finish()
            }
            supportFragmentManager.popBackStack()
            currentFocus?.clearFocus()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.cl_mainscreen, fragment)
            .commitAllowingStateLoss()
    }
}
