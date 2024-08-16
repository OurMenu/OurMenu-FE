package com.example.ourmenu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ourmenu.data.account.AccountLoginData
import com.example.ourmenu.data.account.AccountResponse
import com.example.ourmenu.landing.LandingActivity
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.AccountService
import retrofit2.Call
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({
            // 일정 시간이 지나면 MainActivity로 이동
            autoLogin()
            // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
            // 이동한 다음 사용안함으로 finish 처리
        }, 1000) // 시간 1초 이후 실행
    }

    fun autoLogin(){
        sharedPreferences = applicationContext.getSharedPreferences("AutoLogin", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email",null)
        val password = sharedPreferences.getString("password",null)
        if (email.isNullOrEmpty()||password.isNullOrEmpty()){
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            accountLogin(email, password)
        }
    }

    fun accountLogin(email : String, password : String) {
        NetworkModule.initialize(applicationContext)
        val service = RetrofitObject.retrofit.create(AccountService::class.java)
        val call = service.postAccountLogin(
            AccountLoginData(
                email, password
            )
        )

        call.enqueue(object : retrofit2.Callback<AccountResponse> {
            override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                if (response.isSuccessful) {
                    RetrofitObject.TOKEN = response.body()?.response?.accessToken
                    RetrofitObject.refreshToken = response.body()?.response?.refreshToken
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    this@SplashActivity.finish()
                }else{
                    Log.d("오류","로그인 실패")
                }
            }
            override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}
