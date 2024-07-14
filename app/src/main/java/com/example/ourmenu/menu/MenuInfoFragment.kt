package com.example.ourmenu.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ourmenu.R
import com.example.ourmenu.databinding.FragmentMenuInfoBinding

class MenuInfoFragment : Fragment() {
    lateinit var binding: FragmentMenuInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuInfoBinding.inflate(inflater, container, false)

        return binding.root
    }
}
