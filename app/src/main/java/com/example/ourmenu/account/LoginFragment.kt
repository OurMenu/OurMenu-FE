package com.example.ourmenu.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.ourmenu.MainActivity
import com.example.ourmenu.R
import com.example.ourmenu.data.account.AccountLoginData
import com.example.ourmenu.data.account.AccountResponse
import com.example.ourmenu.data.user.UserImageData
import com.example.ourmenu.data.user.UserPatchResponse
import com.example.ourmenu.databinding.FragmentLoginBinding
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.AccountService
import com.example.ourmenu.retrofit.service.UserService
import com.example.ourmenu.util.Utils
import com.example.ourmenu.util.Utils.showToast
import retrofit2.Call
import retrofit2.Response
import java.net.URL

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnLoginSignup.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.cl_mainscreen, SignupEmailFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.btnLoginLogin.setOnClickListener {

            accountLogin()

        }

        binding.cbLoginShowPassword.setOnCheckedChangeListener { _, isChecked ->
            // 비밀번호가 보여도 font설정에 제대로 되도록 설정
            val currentTypeface = binding.etLoginPassword.typeface

            if (isChecked) {
                // 비밀번호 보이게 하기
                binding.etLoginPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // 비밀번호 숨기기
                binding.etLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            binding.etLoginPassword.typeface = currentTypeface

            // 커서를 text 제일 뒤로 옮기기
            binding.etLoginPassword.setSelection(binding.etLoginPassword.text.length)
        }

        return binding.root
    }

    fun accountLogin() {
        NetworkModule.initialize(requireContext())
        val service = RetrofitObject.retrofit.create(AccountService::class.java)
        val call = service.postAccountLogin(
            AccountLoginData(
                binding.etLoginId.text.toString(),
                binding.etLoginPassword.text.toString()
            )
        )

        call.enqueue(object : retrofit2.Callback<AccountResponse> {
            override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                if (response.isSuccessful) {
                    val sharedPreferences = requireContext().getSharedPreferences("AutoLogin", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("email",binding.etLoginId.text.toString())
                    editor.putString("password",binding.etLoginPassword.text.toString())
                    editor.apply()

                    RetrofitObject.TOKEN = response.body()?.response?.accessToken
                    RetrofitObject.refreshToken = response.body()?.response?.refreshToken
                    val intent = Intent(activity, MainActivity::class.java)

                    // 화면 이동할 때 키보드 숨기기
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etLoginPassword.windowToken, 0)

                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    if(binding.etLoginId.text.toString().contains("@")){
                        binding.etLoginPassword.setBackgroundResource(R.drawable.edittext_bg_error)
                        showToast(requireContext(),R.drawable.ic_error,"비밀번호가 일치하지 않아요.")
                    }else{
                        binding.etLoginId.setBackgroundResource(R.drawable.edittext_bg_error)
                        showToast(requireContext(),R.drawable.ic_error,"존재하지 않는 이메일이에요.")
                    }
                    Log.d("오류", "로그인 실패")
                }
            }

            override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}
