package com.example.ourmenu.menu.menuInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ourmenu.R
import com.example.ourmenu.data.menu.data.MenuImage
import com.example.ourmenu.data.menu.data.MenuInfoData
import com.example.ourmenu.data.menu.response.MenuInfoResponse
import com.example.ourmenu.databinding.FragmentMenuInfoBinding
import com.example.ourmenu.menu.menuInfo.adapter.MenuInfoVPAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuService
import com.example.ourmenu.util.Utils.toWon
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuInfoFragment : Fragment() {
    lateinit var binding: FragmentMenuInfoBinding

    private var groupId = 0
    lateinit var menuIconType: String
    private val imgItems = ArrayList<MenuImage>()

    private val retrofit = RetrofitObject.retrofit
    private val menuService = retrofit.create(MenuService::class.java)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMenuInfoBinding.inflate(inflater, container, false)
        Log.d("12", "13333323")

        getMenuInfo()
        // 뷰페이져 어댑터
//        initViewPager2Adapter()
        // 온클릭 리스너
        initOnClickListener()

        return binding.root
    }

    private fun getMenuInfo() {
        var groupId = arguments?.getInt("groupId")!!
        if (groupId == -1) return

        menuService.getMenuInfo(groupId = groupId).enqueue(object : Callback<MenuInfoResponse> {
            override fun onResponse(call: Call<MenuInfoResponse>, response: Response<MenuInfoResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val menuInfoData = result?.response
                    menuInfoData?.let {
                        initData(it)
                    }
                }
            }

            override fun onFailure(call: Call<MenuInfoResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun initData(menuInfoData: MenuInfoData) {

        groupId = menuInfoData.groupId
        menuIconType = menuInfoData.menuIconType

        // 메뉴 이름, 가격
        binding.tvMenuInfoMenuTitle.text = menuInfoData.menuTitle
        binding.tvMenuInfoMenuPrice.text = toWon(menuInfoData.menuPrice)
        // TODO 메뉴 폴더 칩
        imgItems.addAll(menuInfoData.menuImages)
        initViewPager2Adapter()

        // 메모
        binding.tvMenuInfoMemoTitle.text = menuInfoData.menuMemoTitle
        binding.tvMenuInfoMemoContent.text = menuInfoData.menuMemo

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
//        val dummyItems = ArrayList<String>()
//        for (i in 1..6) {
//            dummyItems.add(
//                "1",
//            )

        binding.vpMenuInfoMenuImage.adapter = MenuInfoVPAdapter(imgItems,requireContext())
        binding.vpMenuInfoMenuImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.idcMenuInfoIndicator.attachTo(binding.vpMenuInfoMenuImage)
    }
}
}
