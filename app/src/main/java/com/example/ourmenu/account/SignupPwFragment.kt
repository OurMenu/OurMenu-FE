package com.example.ourmenu.account

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.fonts.FontFamily
import android.graphics.fonts.FontStyle
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.example.ourmenu.R
import com.example.ourmenu.data.account.AccountEmailData
import com.example.ourmenu.databinding.FragmentSignupPwBinding
import com.example.ourmenu.retrofit.NetworkModule
import com.example.ourmenu.util.Utils.showToast

class SignupPwFragment : Fragment() {
    lateinit var binding: FragmentSignupPwBinding
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        NetworkModule.initialize(requireContext())
        binding = FragmentSignupPwBinding.inflate(inflater, container, false)
        binding.btnSignupPw.setOnClickListener {
            if (binding.etSignupPasswordEnter.text.length >= 8 && binding.etSignupPasswordEnter.text.matches(Regex("[a-z|A-Z|0-9]+"))) {
                if (binding.etSignupPasswordEnter.text.toString() == binding.etSignupPasswordEnterCheck.text.toString()) {
                    var fragment = SignupNicknameFragment()
                    fragment.email = email
                    fragment.password = binding.etSignupPasswordEnter.text.toString()
                    parentFragmentManager.beginTransaction()
                        .addToBackStack("SignupPw")
                        .replace(R.id.cl_mainscreen, fragment)
                        .commit()
                } else {
                    showToast(requireContext(), R.drawable.ic_error, "비밀번호가 일치하지 않아요.")
                    binding.etSignupPasswordEnter.setBackgroundResource(R.drawable.edittext_bg_error)
                    binding.etSignupPasswordEnterCheck.setBackgroundResource(R.drawable.edittext_bg_error)
                }
            } else {
                showToast(requireContext(), R.drawable.ic_error, "비밀번호 조건을 다시 확인해주세요.")
                binding.etSignupPasswordEnter.setBackgroundResource(R.drawable.edittext_bg_error)

            }
        }
        var flag = true
        binding.cbSignupShowPassword.setOnClickListener {
            if (flag) {

                binding.etSignupPasswordEnter.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.etSignupPasswordEnter.inputType = InputType.TYPE_CLASS_TEXT
                binding.etSignupPasswordEnterCheck.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.etSignupPasswordEnterCheck.inputType = InputType.TYPE_CLASS_TEXT
                binding.etSignupPasswordEnter.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.pretendard_600)
                binding.etSignupPasswordEnterCheck.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.pretendard_600)
                flag = false
            } else {
                binding.etSignupPasswordEnter.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.etSignupPasswordEnterCheck.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.etSignupPasswordEnter.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.pretendard_600)
                binding.etSignupPasswordEnterCheck.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.pretendard_600)
                flag = true
            }
        }

        binding.etSignupPasswordEnter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etSignupPasswordEnter.setBackgroundResource(R.drawable.edittext_bg_default)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.etSignupPasswordEnterCheck.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etSignupPasswordEnterCheck.setBackgroundResource(R.drawable.edittext_bg_default)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        return binding.root
    }

}
