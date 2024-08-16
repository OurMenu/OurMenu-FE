package com.example.ourmenu.account.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.ourmenu.R
import com.example.ourmenu.account.SignupEmailFragment
import com.example.ourmenu.databinding.SpinnerItemBackgroundBinding
import com.example.ourmenu.databinding.SpinnerItemBackgroundEditBinding
import com.example.ourmenu.databinding.SpinnerItemBackgroundNullBinding

class SpinnerAdapter<T>(
    context: Context,
    val fragment: SignupEmailFragment,
) : ArrayAdapter<String>(context, R.layout.spinner_default_background, listOf("텍스트", "직접 입력하기", "2", "3", "4")) {
    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val binding = SpinnerItemBackgroundBinding.inflate(LayoutInflater.from(context), parent, false)
        val nullbinding = SpinnerItemBackgroundNullBinding.inflate(LayoutInflater.from(context), parent, false)
        if (position == 0) {
            return nullbinding.root.apply { height = 0 }
        }
        binding.tvSpinnerItemBackground.text = this.getItem(position)
        return binding.root
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val view = super.getView(position, convertView, parent)
        val editbinding = SpinnerItemBackgroundEditBinding.inflate(LayoutInflater.from(context), parent, false)
        if (position == 0) {
            view.requireViewById<TextView>(R.id.tv_spinner_default_background).apply {
                text = ""
                hint = "email.com"
            }
        } else if (position == 1) {
            editbinding.tvSpinnerItemBackgroundEdit.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                        fragment.adflag = false
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (!s.isNullOrEmpty()) {
                            fragment.adflag = true
                            fragment.flagCheck()
                        } else {
                            fragment.adflag = false
                            fragment.flagCheck()
                        }
                    }
                },
            )
            return editbinding.root
        }
        return view
    }

    fun initListener() {
        val binding = SpinnerItemBackgroundEditBinding.inflate(LayoutInflater.from(context))
        binding.ivShowDropdown.setOnClickListener {
            binding.tvSpinnerItemBackgroundEdit.isEnabled = false
        }
    }

    fun submitList(list: List<String>) {
        this.clear()
        this.addAll(list)
        this.notifyDataSetChanged()
    }
}
