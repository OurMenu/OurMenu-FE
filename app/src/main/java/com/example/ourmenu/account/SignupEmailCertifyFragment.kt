package com.example.ourmenu.account

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.ourmenu.R
import com.example.ourmenu.databinding.FragmentSignupEmailCertifyBinding

class SignupEmailCertifyFragment : Fragment() {
    lateinit var binding: FragmentSignupEmailCertifyBinding
    lateinit var EmailAndCode : Bundle
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
        Log.d("확인", EmailAndCode.getSerializable("email").toString())
        binding.btnSignupEmailSertify.setOnClickListener{
            binding.etSignupCode6.clearFocus()
            parentFragmentManager.beginTransaction()
                .addToBackStack("SignupEmailSertify")
                .replace(R.id.cl_mainscreen, SignupPwFragment())
                .commit()
        }
        binding.etSignupCode1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(!binding.etSignupCode1.text.isNullOrEmpty()){
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
                if(!binding.etSignupCode2.text.isNullOrEmpty()){
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
                if(!binding.etSignupCode3.text.isNullOrEmpty()){
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
                if(!binding.etSignupCode4.text.isNullOrEmpty()){
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
                if(!binding.etSignupCode5.text.isNullOrEmpty()){
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
}
