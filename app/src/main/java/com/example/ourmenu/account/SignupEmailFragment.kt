package com.example.ourmenu.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Spinner
import com.example.ourmenu.adapters.SpinnerAdapter
import androidx.fragment.app.Fragment
import com.example.ourmenu.R
import com.example.ourmenu.databinding.FragmentSignupEmailBinding
import com.example.ourmenu.databinding.SpinnerItemBackgroundEditBinding

class SignupEmailFragment : Fragment() {
    lateinit var binding: FragmentSignupEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSignupEmailBinding.inflate(inflater, container, false)
        val bindingEdit = SpinnerItemBackgroundEditBinding.inflate(inflater,container,false)
        val adapter = SpinnerAdapter<String>(requireContext())
        adapter.initListener()
        adapter.setDropDownViewResource(R.layout.spinner_item_background)
        binding.spnEmail.adapter = adapter
        bindingEdit.tvSpinnerItemBackgroundEdit.setOnClickListener{
            bindingEdit.tvSpinnerItemBackgroundEdit.isFocusedByDefault = true
            bindingEdit.flSpinnerItemBackgroundEdit.isFocusableInTouchMode = true
            bindingEdit.tvSpinnerItemBackgroundEdit.isFocusableInTouchMode = true
            bindingEdit.flSpinnerItemBackgroundEdit.requestFocus()
        }

        return binding.root
    }
}
