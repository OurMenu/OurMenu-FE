package com.example.ourmenu.account

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.ourmenu.R
import com.example.ourmenu.data.account.AccountConfirmCodeData
import com.example.ourmenu.data.account.AccountEmailCodeData
import com.example.ourmenu.data.account.AccountEmailData
import com.example.ourmenu.data.account.AccountEmailResponse
import com.example.ourmenu.data.account.AccountResponse
import com.example.ourmenu.databinding.FragmentSignupEmailCertifyBinding
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.AccountService
import com.example.ourmenu.util.Utils.showToast
import retrofit2.Call
import retrofit2.Response

class SignupEmailCertifyFragment : Fragment() {
    lateinit var binding: FragmentSignupEmailCertifyBinding
    var EmailAndCode: Bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().currentFocus?.clearFocus()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSignupEmailCertifyBinding.inflate(inflater, container, false)
        NetworkModule.initialize(requireContext())
        binding.btnSignupEmailSertify.setOnClickListener {
            confirmCode()
        }
        binding.etSignupCode1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!binding.etSignupCode1.text.isNullOrEmpty()) {
                    binding.etSignupCode2.requestFocus()
                }
            }
        })
        binding.etSignupCode2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!binding.etSignupCode2.text.isNullOrEmpty()) {
                    binding.etSignupCode3.requestFocus()
                }
            }
        })
        binding.etSignupCode3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!binding.etSignupCode3.text.isNullOrEmpty()) {
                    binding.etSignupCode4.requestFocus()
                }
            }
        })
        binding.etSignupCode4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!binding.etSignupCode4.text.isNullOrEmpty()) {
                    binding.etSignupCode5.requestFocus()
                }
            }
        })
        binding.etSignupCode5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!binding.etSignupCode5.text.isNullOrEmpty()) {
                    binding.etSignupCode6.requestFocus()
                }
            }
        })
        return binding.root
    }

    override fun onResume() {
        requireActivity().currentFocus?.clearFocus()
        binding.etSignupCode6.clearFocus()
        super.onResume()
    }

    fun confirmCode() {
        val confirmCode =
            binding.etSignupCode1.text.toString() + binding.etSignupCode2.text.toString() + binding.etSignupCode3.text.toString() + binding.etSignupCode4.text.toString() + binding.etSignupCode5.text.toString() + binding.etSignupCode6.text.toString()
        val service = RetrofitObject.retrofit.create(AccountService::class.java)
        val email = EmailAndCode.getString("email")
        val call = email?.let { AccountConfirmCodeData(it, confirmCode) }?.let { service.postAccountCode(it) }
        call?.enqueue(object : retrofit2.Callback<AccountResponse> {
            override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                if (response.isSuccessful) {
                    parentFragmentManager.beginTransaction()
                        .addToBackStack("SignupEmailCertify")
                        .replace(R.id.cl_mainscreen, SignupPwFragment().apply {
                            if (email != null) {
                                this.email = email
                            }
                        })
                        .commit()
                } else {
                    showToast(requireContext(), R.drawable.ic_error, "인증 코드가 일치하지 않습니다.")
                }

            }

            override fun onFailure(call: Call<AccountResponse>, t: Throwable) {

            }

        })
    }
}
