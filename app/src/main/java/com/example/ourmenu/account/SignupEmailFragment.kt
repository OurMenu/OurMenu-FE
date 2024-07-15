package com.example.ourmenu.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.ourmenu.R
import com.example.ourmenu.databinding.FragmentSignupEmailBinding

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
        var emaildummylist : List<String> = listOf("","2","3","4")
        var spinnerAdapter = ArrayAdapter(this.requireContext(),R.layout.spinner_item,emaildummylist)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spnEmail.adapter = spinnerAdapter

        return binding.root
    }
}
