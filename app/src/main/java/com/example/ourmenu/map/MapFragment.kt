package com.example.ourmenu.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ourmenu.R
import com.example.ourmenu.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    lateinit var binding : FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapBinding.inflate(inflater, container, false)

        return binding.root

    }

}
