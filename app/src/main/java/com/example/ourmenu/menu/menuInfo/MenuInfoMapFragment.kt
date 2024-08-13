package com.example.ourmenu.menu.menuInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.ourmenu.databinding.FragmentMenuInfoMapBinding
import com.example.ourmenu.util.Utils.loadToNaverMap
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MenuInfoMapFragment : Fragment() {
    lateinit var binding: FragmentMenuInfoMapBinding
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMenuInfoMapBinding.inflate(inflater, container, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.clMenuInfoMapBottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int,
                ) {
                    // Do something when state changes
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float,
                ) {
                    adjustButtonPosition()
                }
            },
        )

        binding.clMenuInfoMapGotoMapBtn.setOnClickListener {
            loadToNaverMap(requireContext(), 37.5666102, 126.9783881, "PlaceName")
        }

        binding.ivMenuInfoMapBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun adjustButtonPosition() {
        val buttonHeight = binding.clMenuInfoMapGotoMapBtn.height
        val bottomSheetHeight = binding.clMenuInfoMapBottomSheet.height
        val bottomSheetTop = binding.clMenuInfoMapBottomSheet.top
        val parentHeight = binding.root.height

        val newButtonY = bottomSheetTop - buttonHeight - 42

        binding.clMenuInfoMapGotoMapBtn.y = newButtonY.toFloat()
    }
}
