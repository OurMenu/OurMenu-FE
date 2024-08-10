package com.example.ourmenu.util

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.icu.text.DecimalFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.ourmenu.R
import com.example.ourmenu.account.SignupPwFragment
import com.example.ourmenu.data.account.AccountConfirmCodeData
import com.example.ourmenu.data.account.AccountRefreshTokenData
import com.example.ourmenu.data.account.AccountResponse
import com.example.ourmenu.databinding.ToastMessageBgBinding
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.AccountService
import retrofit2.Call
import retrofit2.Response

object Utils {
    fun dpToPx(
        context: Context,
        dp: Int,
    ): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun isValidPassword(password: String): Boolean {
        // 영문, 숫자 포함 8자 이상인지 확인
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
        return password.matches(Regex(passwordPattern))
    }

    fun showToast(
        context: Context,
        icon: Int,
        message: String,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val toastBinding: ToastMessageBgBinding = ToastMessageBgBinding.inflate(layoutInflater)

        toastBinding.ivToastMessage.setImageResource(icon)
        toastBinding.tvToastMessage.text = message

        val toast = Toast(context)
        toast.view = toastBinding.root
        toast.duration = Toast.LENGTH_SHORT

        // Toast 메시지를 화면 상단으로부터 128dp 떨어진 위치에 표시
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, dpToPx(context, 128))

        toast.show()
    }

    fun hideKeyboard(
        context: Context,
        view: View,
    ) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    inline fun <reified T> getTypeOf(): Class<T> {
        return T::class.java
    }

    fun applyBlurEffect(viewGroup: ViewGroup) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            viewGroup.setRenderEffect(RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP))
        }
    }

    fun removeBlurEffect(viewGroup: ViewGroup) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            viewGroup.setRenderEffect(null)
        }
    }

    fun View.viewGone() {
        this.visibility = View.GONE
    }

    fun View.viewVisible() {
        this.visibility = View.VISIBLE
    }

    fun View.viewInvisible() {
        this.visibility = View.INVISIBLE
    }

    fun toWon(price: Any): String {
        val dec = DecimalFormat("#,###원")

        when (price) {
            is Int -> {
                return dec.format(price)
            }

            is String -> {
                // 숫자만 남김
                val digitOnly = price.filter { it.isDigit() }
                val number = digitOnly.toIntOrNull() ?: 0
                return dec.format(digitOnly)
            }

            else -> return "0원"
        }
    }

    fun reissueToken(refreshToken:String, context: Context) : ArrayList<String>?{
        NetworkModule.initialize(context)
        val service = RetrofitObject.retrofit.create(AccountService::class.java)
        val call = service.postAccountReissue(AccountRefreshTokenData(refreshToken))
        var result : ArrayList<String>? = null

        call.enqueue(object : retrofit2.Callback<AccountResponse> {
            override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                if (response.isSuccessful) {
                    result = response.body()?.response?.let { arrayListOf<String>(it.accessToken,
                        response.body()?.response!!.refreshToken)}
                } else {
                    showToast(context, R.drawable.ic_error, "토큰을 불러올 수 없습니다.")
                }
            }
            override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                Log.d("오류","서버 오류")
            }
        })
        return result
    }
}
