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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.ourmenu.R
import com.example.ourmenu.data.community.StrResponse
import com.example.ourmenu.data.user.PasswordResponse
import com.example.ourmenu.data.user.email
import com.example.ourmenu.databinding.FragmentSignupEmailBinding
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.UserService
import com.example.ourmenu.util.Utils
import retrofit2.Call
import retrofit2.Response

class FindPasswordFragment : Fragment(){
        lateinit var binding: FragmentSignupEmailBinding
        var ddflag = false
        var adflag = false
        var idflag = false

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
            binding.tvSignupEmailRequired.text = "메일로 임시 비밀번호를 보내드려요."
            binding.btnSignupEmail.text = "메일 보내기"
            binding.btnSignupEmail.setOnClickListener {
                userFindPassword()
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
                    if (!s.isNullOrEmpty()&&s.contains(".")) {
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
    fun userFindPassword(){
        Utils.showToast(requireContext(), R.drawable.ic_complete, "새로운 비밀번호를 보내드렸어요!")
        NetworkModule.initialize(requireContext())
        val service = RetrofitObject.retrofit.create(UserService::class.java)
        val call = service.postTemporaryPassword(
            email(binding.etSignupEmailId.text.toString()+"@"+binding.etSignupEmail.text.toString()
            )
        )

        call.enqueue(object : retrofit2.Callback<PasswordResponse> {
            override fun onResponse(call: Call<PasswordResponse>, response: Response<PasswordResponse>) {
                if(response.isSuccessful){
                    Log.d("오류",response.body()?.response?.password.toString())
                    FindPasswordDialog().show(parentFragmentManager,"")
                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.cl_mainscreen,LoginFragment())
                        .commitAllowingStateLoss()
                }
            }

            override fun onFailure(call: Call<PasswordResponse>, t: Throwable) {
            }

        })

    }
    fun flagCheck() {
        if (idflag and adflag) {
            binding.btnSignupEmail.setEnabled(true)
        } else {
            binding.btnSignupEmail.setEnabled(false)
        }
    }
}
