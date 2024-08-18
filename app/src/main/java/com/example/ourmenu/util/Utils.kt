package com.example.ourmenu.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.ourmenu.R
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

    inline fun <reified T> getTypeOf(): Class<T> = T::class.java

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

            is Float -> {
                return dec.format(price.toInt())
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

    fun loadToNaverMap(
        context: Context,
        lat: Double,
        lng: Double,
        name: String,
    ) {
        // 위도, 경도, 위치 이름을 받아서 URL 생성
        val url = "nmap://place?lat=$lat&lng=$lng&name=$name"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        val installCheck =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
                )
            } else {
                context.packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                    PackageManager.GET_META_DATA,
                )
            }

        // 네이버맵이 설치되어 있다면 앱으로 연결, 설치되어 있지 않다면 스토어로 이동
        if (installCheck.isEmpty()) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.nmap")))
        } else {
            context.startActivity(intent)
        }
    }

    fun getSmallMapPin(menuIconType: String?): Int =
        when (menuIconType ?: "0") { // null일 경우 "0"으로 대체
            "0" -> R.drawable.ic_map_01_s
            "1" -> R.drawable.ic_map_02_s
            "2" -> R.drawable.ic_map_03_s
            "3" -> R.drawable.ic_map_04_s
            "4" -> R.drawable.ic_map_05_s
            "5" -> R.drawable.ic_map_06_s
            "6" -> R.drawable.ic_map_07_s
            "7" -> R.drawable.ic_map_08_s
            "8" -> R.drawable.ic_map_09_s
            "9" -> R.drawable.ic_map_10_s
            "10" -> R.drawable.ic_map_11_s
            "11" -> R.drawable.ic_map_12_s
            "12" -> R.drawable.ic_map_13_s
            "13" -> R.drawable.ic_map_14_s
            "14" -> R.drawable.ic_map_15_s
            "15" -> R.drawable.ic_map_16_s
            "16" -> R.drawable.ic_map_17_s
            "17" -> R.drawable.ic_map_18_s
            "18" -> R.drawable.ic_map_19_s
            "19" -> R.drawable.ic_map_20_s
            else -> R.drawable.ic_map_01_s
        }

    fun getLargeMapPin(menuIconType: String?): Int =
        when (menuIconType ?: "0") { // null일 경우 "0"으로 대체
            "0" -> R.drawable.ic_map_01_l
            "1" -> R.drawable.ic_map_02_l
            "2" -> R.drawable.ic_map_03_l
            "3" -> R.drawable.ic_map_04_l
            "4" -> R.drawable.ic_map_05_l
            "5" -> R.drawable.ic_map_06_l
            "6" -> R.drawable.ic_map_07_l
            "7" -> R.drawable.ic_map_08_l
            "8" -> R.drawable.ic_map_09_l
            "9" -> R.drawable.ic_map_10_l
            "10" -> R.drawable.ic_map_11_l
            "11" -> R.drawable.ic_map_12_l
            "12" -> R.drawable.ic_map_13_l
            "13" -> R.drawable.ic_map_14_l
            "14" -> R.drawable.ic_map_15_l
            "15" -> R.drawable.ic_map_16_l
            "16" -> R.drawable.ic_map_17_l
            "17" -> R.drawable.ic_map_18_l
            "18" -> R.drawable.ic_map_19_l
            "19" -> R.drawable.ic_map_20_l
            else -> R.drawable.ic_map_01_l
        }

    fun ImageView.loadImageFromUrl(imageUrl: String) {
        val imageLoader =
            ImageLoader
                .Builder(this.context)
                .componentRegistry {
                    add(SvgDecoder(context))
                }.build()

        val imageRequest =
            ImageRequest
                .Builder(this.context)
                .crossfade(true)
                .crossfade(300)
                .data(imageUrl)
                .target(
                    onSuccess = { result ->
                        val bitmap = (result as BitmapDrawable).bitmap
                        this.setImageBitmap(bitmap)
                    },
                ).build()

        imageLoader.enqueue(imageRequest)
    }

    fun reissueToken(context: Context) {
        NetworkModule.initialize(context)
        val service = RetrofitObject.retrofit.create(AccountService::class.java)
        val call = service.postAccountReissue(AccountRefreshTokenData(RetrofitObject.refreshToken!!))

        call.enqueue(
            object : retrofit2.Callback<AccountResponse> {
                override fun onResponse(
                    call: Call<AccountResponse>,
                    response: Response<AccountResponse>,
                ) {
                    if (response.isSuccessful) {
                        RetrofitObject.TOKEN = response.body()?.response?.accessToken
                        RetrofitObject.refreshToken = response.body()?.response?.refreshToken
                    } else {
                        Log.d("오류", "refresh 불가")
                        showToast(context, R.drawable.ic_error, "토큰을 불러올 수 없습니다.")
                    }
                }

                override fun onFailure(
                    call: Call<AccountResponse>,
                    t: Throwable,
                ) {
                    Log.d("오류", "서버 오류")
                }
            },
        )
    }

    fun String?.isNotNull() : Boolean{

        return this != "" && this != "null" && this != null
    }

}
