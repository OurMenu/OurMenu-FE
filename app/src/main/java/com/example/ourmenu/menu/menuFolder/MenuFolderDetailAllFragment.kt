package com.example.ourmenu.menu.menuFolder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.ourmenu.R
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.menu.response.MenuArrayResponse
import com.example.ourmenu.databinding.FragmentMenuFolderDetailAllBinding
import com.example.ourmenu.menu.adapter.MenuFolderAllFilterSpinnerAdapter
import com.example.ourmenu.menu.adapter.MenuFolderDetailAllRVAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuService
import com.example.ourmenu.util.Utils.viewGone
import com.example.ourmenu.util.Utils.viewVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class MenuFolderDetailAllFragment : Fragment() {

    lateinit var binding: FragmentMenuFolderDetailAllBinding
    val chipItems = ArrayList<Chip>()
    var tagItems = ArrayList<String>()
    lateinit var dummyItems: ArrayList<MenuData>
    lateinit var menuItems: ArrayList<MenuData>
    lateinit var sortedMenuItems: ArrayList<MenuData>
    lateinit var rvAdapter: MenuFolderDetailAllRVAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    // 바텀시트 chip 관련
    val bottomSheetChipItems = ArrayList<Chip>()
    lateinit var checkedChipKind: Chip
    lateinit var checkedChipCountry: Chip
    lateinit var checkedChipTaste: Chip
    lateinit var checkedChipCondition: Chip
    private var priceRange: ArrayList<Int> = arrayListOf(0, 0)

    private val retrofit = RetrofitObject.retrofit
    private val menuService = retrofit.create(MenuService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMenuFolderDetailAllBinding.inflate(layoutInflater)

//        initialChips()
        initSpinner()
        initBottomSheet()

//        getMenuItems()

        initListener()
        initRVAdapter()




        return binding.root
    }

    private fun getMenuItems() {
        menuService.getMenus(
            tags = null,
            title = null,
            menuFolderId = null, // 전체 메뉴판일 때에는 null
            page = null,
            size = null,
            minPrice = "", maxPrice = ""

        ).enqueue(object : Callback<MenuArrayResponse> {
            override fun onResponse(call: Call<MenuArrayResponse>, response: Response<MenuArrayResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.response?.let {
                        menuItems = result.response
                        sortedMenuItems = result.response
                    }
                }
            }

            override fun onFailure(call: Call<MenuArrayResponse>, t: Throwable) {
                Log.d("AllMenu", t.toString())
            }

        })
    }

    private fun initRVAdapter() {
        val dummyItems = ArrayList<MenuData>()
        for (i in 1..9) {
            dummyItems.add(
                MenuData(
                    groupId = 0,
                    menuImgUrl = "",
                    menuPrice = 0,
                    menuTitle = "menu$i",
                    placeAddress = "address$i",
                    placeTitle = "place$i"
                ),
            )
        }

        rvAdapter =
            MenuFolderDetailAllRVAdapter(dummyItems, requireContext())
        binding.rvMfdaMenu.adapter = rvAdapter

    }

    private fun initSpinner() {
        val adapter =
            MenuFolderAllFilterSpinnerAdapter<String>(requireContext(), arrayListOf("이름순", "등록순", "가격순"))
        adapter.setDropDownViewResource(R.layout.spinner_item_background)
        binding.spnMfdaFilter.adapter = adapter
        binding.spnMfdaFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                adapter.selectedPos = position
//                sortBySpinner(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun sortBySpinner(position: Int) {
        when (position) {
            0 -> { // 이름순, 이름이 같아면 가격순
                sortedMenuItems.sortWith(compareBy<MenuData> { it.menuTitle }.thenBy { it.menuPrice })
            }

            1 -> { // 등록순
                sortedMenuItems.sortBy { it.menuTitle }
            }

            2 -> { // 가격순, 가격이 같다면 이름순
                sortedMenuItems.sortWith(compareBy<MenuData> { it.menuPrice }.thenBy { it.menuTitle })
            }

            else -> return
        }
        rvAdapter.updateList(sortedMenuItems)


    }

    private fun initListener() {

        binding.ivMfdaBack.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
                or BottomSheetBehavior.STATE_COLLAPSED
            )
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            else
                requireActivity().finish()
        }
//
//        binding.chipMfdaAll.setOnClickListener {
//
//            val menuFolderDetailAllFilterFragment = MenuFolderDetailAllFilterFragment(this)
//            if (chipItems.size > 0) {
//                val bundle = Bundle()
//                for (i in 0 until chipItems.size) {
//                    bundle.putString("chip$i", chipItems[i].text.toString())
//                }
//                menuFolderDetailAllFilterFragment.arguments = bundle
//            }
//
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.menu_folder_frm, menuFolderDetailAllFilterFragment)
//                .addToBackStack("MenuFolderDetailAllFragment")
//                .commit()
//        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.menuFolderAllFilter)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.btnMfdaAddMenu.viewVisible()
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomSheetExpanded()
                        binding.btnMfdaAddMenu.viewGone()
                    }

                    else -> {
                        return
                    }
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })
        binding.chipMfdaAll.setOnClickListener {
            Log.d("fitl", "fit")
//            bottomSheetBehavior.maxHeight = dpToPx(requireContext(), 740)
//            binding.menuFolderAllFilter.layoutParams.height = dpToPx(requireContext(), 720)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.btnMfdaAddMenu.viewGone()
        }

        initBottomSheetChips()
        initRangeSlider()
        initBottomSheetListener()
    }

    // 바텀 시트 올라왔을 때 칩들 체크 설정
    private fun bottomSheetExpanded() {

        for (i in 0 until binding.cgMfdaKind.childCount) {
            val chip = binding.cgMfdaKind.getChildAt(i) as Chip
            for (j in 0 until chipItems.size) {
                if (chipItems[j].text == chip.text) {
                    setChipSelected(chip, 1)
                }
            }
        }

        for (i in 0 until binding.cgMfdaCountry.childCount) {
            val chip = binding.cgMfdaCountry.getChildAt(i) as Chip
            for (j in 0 until chipItems.size) {
                if (chipItems[j].text == chip.text) {
                    setChipSelected(chip, 2)
                }
            }
        }

        for (i in 0 until binding.cgMfdaTaste.childCount) {
            val chip = binding.cgMfdaTaste.getChildAt(i) as Chip
            for (j in 0 until chipItems.size) {
                if (chipItems[j].text == chip.text) {
                    setChipSelected(chip, 3)
                }
            }
        }


        for (i in 0 until binding.cgMfdaCondition.childCount) {
            val chip = binding.cgMfdaCondition.getChildAt(i) as Chip
            for (j in 0 until chipItems.size) {
                if (chipItems[j].text == chip.text) {
                    setChipSelected(chip, 4)
                }
            }
        }
    }

    // 기존 선택된 chip 을 초기화하고, 새로운걸 선택된 것 처럼 표시하고 선택된 것들을 저장함
    // 1개만 선택가능.
    private fun initBottomSheetChips() {
        // 기본값으로 초기화
        checkedChipKind = Chip(requireContext())
        checkedChipCountry = Chip(requireContext())
        checkedChipTaste = Chip(requireContext())
        checkedChipCondition = Chip(requireContext())

        for (i in 0 until binding.cgMfdaKind.childCount) {
            val chip = binding.cgMfdaKind.getChildAt(i) as Chip
            chip.setOnClickListener {
                setChipSelected(chip, 1)
            }
        }

        for (i in 0 until binding.cgMfdaCountry.childCount) {
            val chip = binding.cgMfdaCountry.getChildAt(i) as Chip
            chip.setOnClickListener {
                chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.Primary_500_main)
                chip.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
                chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White))
                setChipSelected(chip, 2)
            }
        }

        for (i in 0 until binding.cgMfdaTaste.childCount) {
            var chip = binding.cgMfdaTaste.getChildAt(i) as Chip
            chip.setOnClickListener {
                setChipSelected(chip, 3)
            }
        }


        for (i in 0 until binding.cgMfdaCondition.childCount) {
            val chip = binding.cgMfdaCondition.getChildAt(i) as Chip
            chip.setOnClickListener {
                setChipSelected(chip, 4)
            }
        }
    }

    private fun setChipSelected(newChip: Chip, flag: Int) {
        // 선택되어있으면 비선택으로
        if (newChip.chipBackgroundColor ==
            ContextCompat.getColorStateList(requireContext(), R.color.Primary_500_main)
        ) {

            newChip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
            newChip.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
            newChip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black))

            when (flag) {
                1 -> {
                    checkedChipKind = Chip(requireContext())
                }

                2 -> {
                    checkedChipCountry = Chip(requireContext())

                }

                3 -> {
                    checkedChipTaste = Chip(requireContext())

                }

                else -> {
                    checkedChipCondition = Chip(requireContext())

                }
            }

            // 비선택되어있으면 선택으로
        } else {
            Log.d("1", "4242")
            newChip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.Primary_500_main)
            newChip.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
            newChip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White))

            when (flag) {
                1 -> {
                    newChip.chipBackgroundColor =
                        ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
                    newChip.chipIconTint =
                        ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
                    newChip.setTextColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.Neutral_Black
                        )
                    )
                    checkedChipKind.text = newChip.text
                    checkedChipKind.chipIcon = newChip.chipIcon

                }

                2 -> {
                    newChip.chipBackgroundColor =
                        ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
                    newChip.chipIconTint =
                        ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
                    newChip.setTextColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.Neutral_Black
                        )
                    )
                    checkedChipCountry.text = newChip.text
                    checkedChipCountry.chipIcon = newChip.chipIcon
                }

                3 -> {
                    newChip.chipBackgroundColor =
                        ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
                    newChip.chipIconTint =
                        ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
                    newChip.setTextColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.Neutral_Black
                        )
                    )
                    checkedChipTaste.text = newChip.text
                    checkedChipTaste.chipIcon = newChip.chipIcon
                }

                else -> {
                    newChip.chipBackgroundColor =
                        ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
                    newChip.chipIconTint =
                        ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
                    newChip.setTextColor(
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.Neutral_Black
                        )
                    )
                    checkedChipCondition.text = newChip.text
                    checkedChipCondition.chipIcon = newChip.chipIcon
                }
            }
        }
    }

    private fun initBottomSheetListener() {

        binding.btnMfdaInitialization.setOnClickListener {
            Log.d("init", binding.btnMfdaInitialization.text.toString())
            // 모두 초기화
            initialChips()
        }

        binding.btnMfdaApply.setOnClickListener {
            Log.d("apply", binding.btnMfdaApply.text.toString())
//            val chips = ArrayList<Chip>()
//            val chipKind: Chip
//            val chipCountry: Chip
//            val chipTaste: Chip
//            val chipCondition: Chip
//            if (isCheckedChip(checkedChipKind)) {
//                chipKind = checkedChipKind
//                chips.add(chipKind)
//            }
//            if (isCheckedChip(checkedChipCountry)) {
//                chipCountry = checkedChipCountry
//                chips.add(checkedChipCountry)
//            }
//            if (isCheckedChip(checkedChipTaste)) {
//                chipTaste = checkedChipTaste
//                chips.add(checkedChipTaste)
//            }
//            if (isCheckedChip(checkedChipCondition)) {
//                chipCondition = checkedChipCondition
//                chips.add(checkedChipCondition)
//            }
//            setChips(chips)
            applyChipss()

        }
    }

    // 초기화 버튼
    private fun initialChips() {

//        if (chipItems.size > 0) {
//            for (i in 0 until chipItems.size) {
//                // 부모 뷰에서 제거 후 붙이기
//                val parent = chipItems[i].parent as ViewGroup
//                parent.removeView(chipItems[i])
//                binding.cgMfda.addView(
//                    chipItems[i]
//                )
//                tagItems.add(
//                    chipItems[i].text.toString()
//                )
//                binding.cgMfda.removeView(chipItems[i])
//            }
//        }
        binding.cgMfdaKind
    }

    // 적용하기
    private fun applyChipss() {
        binding.chipMfdaKind

        binding.chipMfdaCountry

        binding.chipMfdaTaste

        binding.chipMfdaCondition
    }

    // 체크되어있으면 true 리턴
    private fun isCheckedChip(chip: Chip, chips: ArrayList<Chip>) {
        if (chip.chipBackgroundColor ==
            ContextCompat.getColorStateList(requireContext(), R.color.Primary_500_main)
        ) {
            chips.add(chip)

        } else {

        }
    }

    // 필터 프래그먼트에서 추가
    private fun setChips(chips: ArrayList<Chip>) {
        for (i in 0 until chips.size) {
            chips[i].chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
            chips[i].chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
            chips[i].setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black))
        }
//        chipItems = chips
    }

    @SuppressLint("SetTextI18n")
    private fun initRangeSlider() {
        // TODO 회의 후 자세한 수치 조정

        binding.rsMfdaRangeSlider.run {
            valueFrom = 0f // valueFrom , valueTo : 슬라이더가 가질 수 있는 총 범위
            valueTo = 100f
            setValues(0f, 100f) // 슬라이더가 시작할 초기 범위
            stepSize = 10f // 슬라이더 간격 사이즈
            setCustomThumbDrawablesForValues(
                R.drawable.ic_slider_thumb_left, R.drawable.ic_slider_thumb_right
            ) // thumb 설정
            trackActiveTintList = ContextCompat.getColorStateList(requireContext(), R.color.Primary_500_main)!!
            // 활성화 색상
            trackInactiveTintList = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_300)!!
            // 비활성화 색상
            labelBehavior = LabelFormatter.LABEL_GONE
            // 라벨 없애기
        }

        binding.rsMfdaRangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
                Log.d("start", "start")
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                Log.d("stop", "stop")

            }
        })

        binding.rsMfdaRangeSlider.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            val values = slider.values
            val wonFormat = NumberFormat.getNumberInstance(Locale("ko", "KR"))
            wonFormat.maximumFractionDigits = 0
            wonFormat.minimumFractionDigits = 0
            binding.tvMfdaStartPrice.text = "${wonFormat.format(values[0] * 1000)}원"
            binding.tvMfdaEndPrice.text = "${wonFormat.format(values[1] * 1000)}원"
        })

    }
}
