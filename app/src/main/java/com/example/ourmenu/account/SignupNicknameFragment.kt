package com.example.ourmenu.account

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ourmenu.MainActivity
import com.example.ourmenu.R
import com.example.ourmenu.data.account.AccountConfirmCodeData
import com.example.ourmenu.data.account.AccountEmailCodeData
import com.example.ourmenu.data.account.AccountEmailData
import com.example.ourmenu.data.account.AccountResponse
import com.example.ourmenu.data.account.AccountSignupData
import com.example.ourmenu.databinding.FragmentSignupNicknameBinding
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.AccountService
import com.example.ourmenu.util.Utils.showToast
import retrofit2.Call
import retrofit2.Response

class SignupNicknameFragment : Fragment() {
    lateinit var binding: FragmentSignupNicknameBinding
    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSignupNicknameBinding.inflate(inflater, container, false)
        binding.btnSignupNickname.setOnClickListener {
            if (binding.etSignupNickname.text.length <= 10 && (binding.etSignupNickname.text.isNotEmpty())) {
                postAccountSignup()
                parentFragmentManager
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.cl_mainscreen, LoginFragment())
                    .commit()
            } else {
                binding.etSignupNickname.setBackgroundResource(R.drawable.edittext_bg_error)
                showToast(requireContext(), R.drawable.ic_error, "최대 10자까지 가능해요!")
            }
        }
        binding.etSignupNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etSignupNickname.setBackgroundResource(R.drawable.edittext_bg_default)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        return binding.root
    }

    fun postAccountSignup() {
        NetworkModule.initialize(requireContext())
        val nickname = binding.etSignupNickname.text.toString()
        val service = RetrofitObject.retrofit.create(AccountService::class.java)
        val call = service.postAccountSignup(AccountSignupData(email, password, nickname))
        call.enqueue(object : retrofit2.Callback<AccountResponse> {
            override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                if (response.isSuccessful) {
                    showToast(requireActivity().applicationContext, R.drawable.ic_complete, "계정 생성 완료!")
                } else {
                    showToast(requireActivity().applicationContext, R.drawable.ic_error, "문제가 있어요. 다시 시도해주세요.")
                }

            }

            override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                showToast(requireContext(), R.drawable.ic_error, "서버 오류ㅣ")
            }

        })
    }
}
