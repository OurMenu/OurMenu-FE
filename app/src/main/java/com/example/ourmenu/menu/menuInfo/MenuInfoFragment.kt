package com.example.ourmenu.menu.menuInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ourmenu.R
import com.example.ourmenu.databinding.FragmentMenuInfoBinding
import com.example.ourmenu.menu.menuInfo.adapter.MenuInfoVPAdapter

class MenuInfoFragment : Fragment() {
    lateinit var binding: FragmentMenuInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMenuInfoBinding.inflate(inflater, container, false)
        Log.d("12", "13333323")

        // 뷰페이져 어댑터
        initViewPager2Adapter()
        // 온클릭 리스너
        initOnClickListener()

        return binding.root
    }

    private fun initOnClickListener() {
        // 닫기 버튼 클릭
        binding.ivMenuInfoClose.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 지도보기 버튼 클릭
        binding.clMenuInfoGotoMapBtn.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .addToBackStack("MenuInfoFragment")
                .replace(R.id.cl_menu_info_container, MenuInfoMapFragment())
                .commit()
        }
    }

    private fun initViewPager2Adapter() {
        val dummyItems = ArrayList<String>()
        for (i in 1..6) {
            dummyItems.add(
                "1",
            )

            binding.vpMenuInfoMenuImage.adapter = MenuInfoVPAdapter(dummyItems)
            binding.vpMenuInfoMenuImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            binding.idcMenuInfoIndicator.attachTo(binding.vpMenuInfoMenuImage)
        }
    }
}
