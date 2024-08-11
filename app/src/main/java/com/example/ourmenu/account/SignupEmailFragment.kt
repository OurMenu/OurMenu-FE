package com.example.ourmenu.account

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.ourmenu.R
import com.example.ourmenu.data.ErrorResponse
import com.example.ourmenu.data.account.AccountEmailCodeData
import com.example.ourmenu.data.account.AccountEmailData
import com.example.ourmenu.data.account.AccountEmailResponse
import com.example.ourmenu.databinding.FragmentSignupEmailBinding
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.AccountService
import com.example.ourmenu.retrofit.service.PlaceService
import com.example.ourmenu.util.Utils.showToast
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST


class SignupEmailFragment : Fragment() {
    lateinit var binding: FragmentSignupEmailBinding
    var idflag = false
    var adflag = false
    var ddflag = false
    var code: AccountEmailCodeData? = null
    lateinit var email : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSignupEmailBinding.inflate(inflater, container, false)
        initDropdown()
        NetworkModule.initialize(requireContext())
        binding.btnSignupEmail.setOnClickListener {
            email = binding.etSignupEmailId.text.toString() + "@" + binding.etSignupEmail.text.toString()
            postEmail(AccountEmailData(email))
        }
        binding.etSignupEmailId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    idflag = true
                    flagCheck()
                } else {
                    idflag = false
                    flagCheck()
                }
            }
        })

        binding.etSignupEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    adflag = true
                    flagCheck()
                } else {
                    adflag = false
                    flagCheck()
                }
            }
        })

        binding.root.setOnClickListener {
            if (activity != null && requireActivity().currentFocus != null) {
                closeKeyboard()
                closeDropdown()
                requireActivity().currentFocus?.clearFocus()
            } else if (binding.llSignupEmailDropdownParent.visibility == View.VISIBLE) {
                closeDropdown()
                requireActivity().currentFocus?.clearFocus()
            } else {
                closeKeyboard()
            }
        }
        return binding.root
    }

    fun flagCheck() {
        if (idflag and adflag) {
            binding.btnSignupEmail.setEnabled(true)
        } else {
            binding.btnSignupEmail.setEnabled(false)
        }
    }

    fun closeDropdown() {
        binding.llSignupEmailDropdownParent.visibility = View.INVISIBLE
        ddflag = false
        binding.ivSignupDropdown.setImageResource(R.drawable.ic_polygon_down)
    }

    fun closeKeyboard() {
        val inputManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    fun initDropdown() {
        binding.etSignupEmail.setOnClickListener {
            binding.etSignupEmail.isCursorVisible = true
        }

        binding.ivSignupDropdown.setOnClickListener {
            if (!ddflag) {
                closeKeyboard()
                binding.etSignupEmail.isFocusable = true
                binding.etSignupEmail.requestFocus()
                binding.etSignupEmail.isCursorVisible = false
                binding.llSignupEmailDropdownParent.visibility = View.VISIBLE
                binding.ivSignupDropdown.setImageResource(R.drawable.ic_polygon_up)
                ddflag = true
            } else if (ddflag) {
                closeDropdown()
                requireActivity().currentFocus?.clearFocus()
            }
        }
        binding.tvSignupEmailDaum.setOnClickListener {
            binding.etSignupEmail.setText(binding.tvSignupEmailDaum.text)
            closeDropdown()
        }
        binding.tvSignupEmailGmail.setOnClickListener {
            binding.etSignupEmail.setText(binding.tvSignupEmailGmail.text)
            closeDropdown()
        }
        binding.tvSignupEmailNate.setOnClickListener {
            binding.etSignupEmail.setText(binding.tvSignupEmailNate.text)
            closeDropdown()
        }
        binding.tvSignupEmailNaver.setOnClickListener {
            binding.etSignupEmail.setText(binding.tvSignupEmailNaver.text)
            closeDropdown()
        }
        binding.tvSignupEmailKakao.setOnClickListener {
            binding.etSignupEmail.setText(binding.tvSignupEmailKakao.text)
            closeDropdown()
        }
        binding.tvSignupEmailSelf.setOnClickListener {
            binding.etSignupEmail.setHint(binding.tvSignupEmailSelf.text)
            binding.etSignupEmail.isCursorVisible = true
            binding.etSignupEmail.requestFocus()
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(binding.etSignupEmail, InputMethodManager.SHOW_IMPLICIT)
            closeDropdown()
        }
    }

    fun postEmail(email: AccountEmailData) {
        val service = RetrofitObject.retrofit.create(AccountService::class.java)
        val call = service.postAccountEmail(email)
        call.enqueue(object : retrofit2.Callback<AccountEmailResponse> {
            override fun onResponse(call: Call<AccountEmailResponse>, response: Response<AccountEmailResponse>) {
                if (response.isSuccessful) {
                    showToast(requireContext(),R.drawable.ic_complete,"잠시만 기다려주세요.")
                    code = response.body()?.response
                    parentFragmentManager.beginTransaction()
                        .addToBackStack("SignupEmail")
                        .replace(
                            R.id.cl_mainscreen,
                            SignupEmailCertifyFragment().apply { this.EmailAndCode.putSerializable("email", email.email) })
                        .commit()
                }
                if (!response.isSuccessful){
                    showToast(requireContext(),R.drawable.ic_error,"이미 사용 중이에요.")
                }
            }

            override fun onFailure(call: Call<AccountEmailResponse>, t: Throwable) {
                Log.d("오류", t.message.toString())
            }
        })
    }
}
