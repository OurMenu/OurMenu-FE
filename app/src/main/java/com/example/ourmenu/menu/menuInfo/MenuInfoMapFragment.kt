package com.example.ourmenu.menu.menuInfo

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.ourmenu.databinding.FragmentMenuInfoMapBinding
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
            searchLoadToNaverMap()
        }

        binding.ivMenuInfoMapBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun searchLoadToNaverMap() {
        // TODO: 위도, 경도, 위치 이름 받아와서 바꾸기
        val url = "nmap://place?lat=37.5666102&lng=126.9783881&name=PlaceName"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        val installCheck =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireContext().packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
                )
            } else {
                requireContext().packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                    PackageManager.GET_META_DATA,
                )
            }

        // 네이버맵이 설치되어 있다면 앱으로 연결, 설치되어 있지 않다면 스토어로 이동
        if (installCheck.isEmpty()) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.nmap")))
        } else {
            startActivity(intent)
        }
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
