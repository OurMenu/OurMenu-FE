package com.example.ourmenu.menu.menuInfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.menu.data.MenuFolderChip
import com.example.ourmenu.data.menu.data.MenuImage
import com.example.ourmenu.data.menu.data.MenuInfoData
import com.example.ourmenu.data.menu.data.MenuTag
import com.example.ourmenu.data.menu.response.MenuInfoResponse
import com.example.ourmenu.databinding.FragmentMenuInfoBinding
import com.example.ourmenu.menu.menuInfo.adapter.MenuInfoVPAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuService
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.toWon
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuInfoFragment : Fragment() {
    lateinit var binding: FragmentMenuInfoBinding
    private var isMemoOpen = false

    private var groupId = 0
    private var menuIconType = ""
    private var menuTags = ArrayList<MenuTag>()
    private var menuImages = ArrayList<MenuImage>()
    private var menuFolders = ArrayList<MenuFolderChip>()

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
        initChips()
        initOnClickListener()

        return binding.root
    }

    private fun getMenuInfo() {
        groupId = arguments?.getInt("groupId")!!
        if (groupId == -1) return
        Log.d("grid", groupId.toString())

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
        menuImages = menuInfoData.menuImages
        initViewPager2Adapter()

        menuTags = menuInfoData.menuTags
        menuFolders = menuInfoData.menuFolders
        initChips()

        // 메모
        binding.tvMenuInfoMemoTitle.text = menuInfoData.menuMemoTitle
        binding.tvMenuInfoMemoContent.text = menuInfoData.menuMemo

    }

    @SuppressLint("SetTextI18n")
    private fun initChips() {
        val folderChip = binding.chipMenuInfoMenuFolder

        // 메뉴 폴더 칩
        for (i in 0 until menuFolders.size) {
            val newChip = copyChip(folderChip, menuFolders[i].menuFolderTitle)
            newChip.textEndPadding = 0f

            binding.cgMenuInfoFolderChip.addView(newChip)
        }

        val defaultChip = binding.chipMenuInfoDefaultTag
        val customChip = binding.chipMenuInfoCustomTag

        // 태그
        for (i in 0 until menuTags.size) {

            // 커스텀 태그
            if (menuTags[i].custom) {
                val newChip = copyChip(customChip, menuTags[i].tagTitle)
                binding.cgMenuInfoCustomTag.addView(newChip)
            }
            // default 태그
            else {
                val newChip = copyChip(defaultChip, menuTags[i].tagTitle)
                binding.cgMenuInfoDefaultTag.addView(newChip)
            }
        }

    }

    private fun copyChip(oldChip: Chip, title: String): Chip {
        val newChip = Chip(requireContext()).apply {
            text = title
            // TODO chip icon 추가
            layoutParams = ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.WRAP_CONTENT,
                ChipGroup.LayoutParams.WRAP_CONTENT
            )
            isClickable = oldChip.isClickable
            isCheckable = oldChip.isCheckable
            letterSpacing = oldChip.letterSpacing
            setTextColor(oldChip.currentTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, oldChip.textSize)
            chipBackgroundColor = oldChip.chipBackgroundColor
            chipCornerRadius = oldChip.chipCornerRadius
            chipIcon = oldChip.chipIcon // 아이콘 복제
            chipIconSize = oldChip.chipIconSize
            chipIconTint = oldChip.chipIconTint
            chipMinHeight = oldChip.chipMinHeight
            iconStartPadding = oldChip.iconStartPadding
            chipEndPadding = oldChip.chipEndPadding
            chipStartPadding = oldChip.chipStartPadding
            chipStrokeColor = oldChip.chipStrokeColor
            chipStrokeWidth = oldChip.chipStrokeWidth
            textStartPadding = oldChip.textStartPadding
            Log.d("cep", chipEndPadding.toString())
            textEndPadding = oldChip.textEndPadding
            Log.d("tep", textEndPadding.toString())
        }
        return newChip
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
                .replace(R.id.menu_info_frm, MenuInfoMapFragment())
                .commit()
        }

        binding.ivMenuInfoMemoDown.setOnClickListener {
            if (isMemoOpen) {
                it.setBackgroundResource(R.drawable.ic_chevron_up)
                binding.tvMenuInfoMemoContent.maxLines = 30
            } else {
                it.setBackgroundResource(R.drawable.ic_chevron_down)
                binding.tvMenuInfoMemoContent.maxLines = 2
            }
        }
    }

    private fun initViewPager2Adapter() {
        val dummyItems = ArrayList<MenuImage>()
        for (i in 1..6) {
            dummyItems.add(
                MenuImage("1")
            )

            binding.vpMenuInfoMenuImage.adapter = MenuInfoVPAdapter(menuImages, requireContext())
            binding.vpMenuInfoMenuImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            binding.idcMenuInfoIndicator.attachTo(binding.vpMenuInfoMenuImage)
        }
    }
}

