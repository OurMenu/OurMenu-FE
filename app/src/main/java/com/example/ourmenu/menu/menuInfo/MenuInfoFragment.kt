package com.example.ourmenu.menu.menuInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ourmenu.R
import com.example.ourmenu.data.BaseResponseWithError
import com.example.ourmenu.data.menu.data.MenuFolderChip
import com.example.ourmenu.data.menu.data.MenuImgUrl
import com.example.ourmenu.data.menu.data.MenuInfoData
import com.example.ourmenu.data.menu.data.MenuTag
import com.example.ourmenu.data.menu.response.MenuInfoResponse
import com.example.ourmenu.databinding.ChipCustomBinding
import com.example.ourmenu.databinding.ChipDefaultBinding
import com.example.ourmenu.databinding.CommunityDeleteDialogBinding
import com.example.ourmenu.databinding.FolderChipBinding
import com.example.ourmenu.databinding.FragmentMenuInfoBinding
import com.example.ourmenu.menu.menuInfo.adapter.MenuInfoVPAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuService
import com.example.ourmenu.util.Utils.applyBlurEffect
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.removeBlurEffect
import com.example.ourmenu.util.Utils.toWon
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuInfoFragment : Fragment() {
    lateinit var binding: FragmentMenuInfoBinding
    private var isMemoOpen = false
    private var isTimeOpen = false

    private var groupId = 0
    private var menuIconType = ""
    private var menuTags = ArrayList<MenuTag>()
    private var menuImages = ArrayList<MenuImgUrl>()
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
        initOnClickListener()

        return binding.root
    }

    private fun getMenuInfo() {
        groupId = arguments?.getInt("groupId")!!
        if (groupId == -1) return
        Log.d("grid", groupId.toString())

        menuService.getMenuInfo(groupId = groupId).enqueue(
            object : Callback<MenuInfoResponse> {
                override fun onResponse(
                    call: Call<MenuInfoResponse>,
                    response: Response<MenuInfoResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val menuInfoData = result?.response
                        menuInfoData?.let {
                            initData(it)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<MenuInfoResponse>,
                    t: Throwable,
                ) {
                    TODO("Not yet implemented")
                }
            },
        )
    }

    private fun initData(menuInfoData: MenuInfoData) {
        groupId = menuInfoData.groupId
        menuIconType = menuInfoData.menuIconType

        // 메뉴 이름, 가격
        binding.tvMenuInfoMenuTitle.text = menuInfoData.menuTitle
        binding.tvMenuInfoMenuPrice.text = toWon(menuInfoData.menuPrice)
        binding.tvMenuInfoStoreName.text = menuInfoData.menuPlaceInfo.placeAddress

        // TODO 메뉴 폴더 칩
        menuImages = menuInfoData.menuImages
        if (menuImages.size == 0) {
            // 빈값 넣어서 일단 어댑터에 넘겨주기
            menuImages = arrayListOf(
                MenuImgUrl("")
            )
        }
        initViewPager2Adapter()

        menuTags = menuInfoData.menuTags
        menuFolders = menuInfoData.menuFolders
        setTags(menuTags)
        setFolderChips(menuFolders)

        // 위치, 영업시간
        binding.tvMenuInfoAddress.text = menuInfoData.menuPlaceInfo.placeAddress
//        binding.tvMenuInfoTime.text = menuInfoData.menuPlaceInfo.placeInfo
        if (menuInfoData.menuPlaceInfo.placeInfo.isEmpty()) {
            // 영업시간 정보가 없을 경우
            binding.tvMenuInfoNoTime.visibility = View.VISIBLE
            binding.tvMenuInfoTime.visibility = View.GONE
            binding.ivMenuInfoShowMore.visibility = View.GONE
        } else {
            // 영업시간 정보가 있을 경우
            binding.tvMenuInfoNoTime.visibility = View.GONE
            binding.tvMenuInfoTime.visibility = View.VISIBLE
            binding.ivMenuInfoShowMore.visibility = View.VISIBLE
            binding.tvMenuInfoTime.text = menuInfoData.menuPlaceInfo.placeInfo
        }

        // 메모 처리
        if (menuInfoData.menuMemoTitle.isEmpty() && menuInfoData.menuMemo.isEmpty()) {
            // 메모 제목과 내용이 모두 비어있을 경우
            binding.tvMenuInfoNoMemo.visibility = View.VISIBLE
            binding.clMenuInfoMemoBox.visibility = View.INVISIBLE
        } else {
            // 메모 제목이나 내용이 하나라도 있을 경우
            binding.tvMenuInfoNoMemo.visibility = View.GONE
            binding.clMenuInfoMemoBox.visibility = View.VISIBLE
            binding.tvMenuInfoMemoTitle.text = menuInfoData.menuMemoTitle
            binding.tvMenuInfoMemoContent.text = menuInfoData.menuMemo
        }
    }

    private fun setTags(tags: ArrayList<MenuTag>) {
        // ChipGroup의 기존 Chip들 제거
        binding.cgMenuInfoDefaultTag.removeAllViews()
        binding.cgMenuInfoCustomTag.removeAllViews()

        if (tags.isEmpty()) {
            // 태그가 없는 경우
            binding.tvMenuInfoNoTag.visibility = View.VISIBLE
        } else {
            // 태그가 있는 경우
            binding.tvMenuInfoNoTag.visibility = View.GONE

            // ChipGroup에 Chip 추가
            for (tag in tags) {
                val inflater = LayoutInflater.from(binding.root.context)
                val customTagBinding = ChipCustomBinding.inflate(inflater, binding.cgMenuInfoCustomTag, false)
                val defaultTagBinding = ChipDefaultBinding.inflate(inflater, binding.cgMenuInfoDefaultTag, false)

                if (tag.custom) {
                    customTagBinding.tvTagDefaultTag.text = tag.tagTitle
                    binding.cgMenuInfoCustomTag.addView(customTagBinding.root)
                } else {
                    defaultTagBinding.tvTagDefaultTag.text = tag.tagTitle
                    binding.cgMenuInfoDefaultTag.addView(defaultTagBinding.root)
                }
            }
        }
    }

    private fun setFolderChips(chips: ArrayList<MenuFolderChip>) {
        // ChipGroup의 기존 Chip들 제거
        binding.cgMenuInfoFolderChip.removeAllViews()

        // ChipGroup에 Chip 추가
        for (chip in chips) {
            val inflater = LayoutInflater.from(binding.root.context)
            val folderChipBinding = FolderChipBinding.inflate(inflater, binding.cgMenuInfoFolderChip, false)

            // TODO: 아이콘 이미지에 따라 설정하기
//            folderChipBinding.ivFolderChipIcon.setImageResource()
            folderChipBinding.tvFolderChipText.text = chip.menuFolderTitle
            binding.cgMenuInfoFolderChip.addView(folderChipBinding.root)
        }
    }

    private fun initOnClickListener() {
        // 닫기 버튼 클릭
        binding.ivMenuInfoBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 지도보기 버튼 클릭
        binding.clMenuInfoGotoMapBtn.setOnClickListener {
            val menuInfoMapFragment =
                MenuInfoMapFragment().apply {
                    arguments =
                        Bundle().apply {
                            putInt("groupId", groupId)
                        }
                }

            parentFragmentManager
                .beginTransaction()
                .addToBackStack("MenuInfoFragment")
                .replace(R.id.menu_info_frm, menuInfoMapFragment)
                .commit()
        }

        binding.clMenuInfoMemoContainer.setOnClickListener {
            if (isMemoOpen) {
                binding.ivMenuInfoMemoDown.setImageResource(R.drawable.ic_chevron_down)
                binding.tvMenuInfoMemoContent.maxLines = 2
                isMemoOpen = false
            } else {
                binding.ivMenuInfoMemoDown.setImageResource(R.drawable.ic_chevron_up)
                binding.tvMenuInfoMemoContent.maxLines = 30
                isMemoOpen = true
            }
        }

        binding.clMenuInfoTimeContainer.setOnClickListener {
            if (isTimeOpen) {
                binding.ivMenuInfoShowMore.setImageResource(R.drawable.ic_chevron_down)
                binding.tvMenuInfoTime.maxLines = 1
                isTimeOpen = false
            } else {
                binding.ivMenuInfoShowMore.setImageResource(R.drawable.ic_chevron_up)
                binding.tvMenuInfoTime.maxLines = 10
                isTimeOpen = true
            }
        }

        binding.ivMenuInfoKebab.setOnClickListener {
            showDeleteDialog()
        }
    }

    // kebab -> 삭제하기
    private fun showDeleteDialog() {
        val rootView = (activity?.window?.decorView as? ViewGroup)?.getChildAt(0) as? ViewGroup
        // 블러 효과 추가
        rootView?.let { applyBlurEffect(it) }

        val dialogBinding = CommunityDeleteDialogBinding.inflate(LayoutInflater.from(context))
        val deleteDialog =
            android.app.AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        deleteDialog.setOnShowListener {
            val window = deleteDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            val params = window?.attributes
            params?.width = dpToPx(requireContext(), 288)
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = params
        }

        // dialog 사라지면 블러효과도 같이 사라짐
        deleteDialog.setOnDismissListener {
            rootView?.let { removeBlurEffect(it) }
        }

        dialogBinding.ivCddClose.setOnClickListener {
            deleteDialog.dismiss()
        }

        dialogBinding.btnCddDelete.setOnClickListener {
            // TODO: 게시글 삭제 API
            deleteDialog.dismiss()
            deleteMenu()
        }

        dialogBinding.btnCddCancel.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }

    private fun deleteMenu() {
        menuService.deleteMenu(groupId).enqueue(
            object : Callback<BaseResponseWithError> {
                override fun onResponse(
                    call: Call<BaseResponseWithError>,
                    response: Response<BaseResponseWithError>,
                ) {
                    if (response.isSuccessful) {
                        requireActivity().finish()
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponseWithError>,
                    t: Throwable,
                ) {
                    Log.d("deleteMenu", t.toString())
                }
            },
        )
    }

    private fun initViewPager2Adapter() {
        val dummyItems = ArrayList<MenuImgUrl>()
        for (i in 1..6) {
            dummyItems.add(
                MenuImgUrl("1"),
            )

            binding.vpMenuInfoMenuImage.adapter = MenuInfoVPAdapter(menuImages, requireContext())
            binding.vpMenuInfoMenuImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL

            binding.idcMenuInfoIndicator.attachTo(binding.vpMenuInfoMenuImage)
        }
    }
}
