package com.example.ourmenu.menu.menuFolder

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.ourmenu.R
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.menu.response.MenuArrayResponse
import com.example.ourmenu.databinding.FragmentMenuFolderDetailAllBinding
import com.example.ourmenu.menu.adapter.MenuFolderAllFilterSpinnerAdapter
import com.example.ourmenu.menu.adapter.MenuFolderDetailAllRVAdapter
import com.example.ourmenu.menu.menuInfo.MenuInfoActivity
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuService
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.toWon
import com.example.ourmenu.util.Utils.viewGone
import com.example.ourmenu.util.Utils.viewVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

class MenuFolderDetailAllFragment : Fragment() {

    lateinit var binding: FragmentMenuFolderDetailAllBinding
    private val menuItems = ArrayList<MenuData>()
    private val sortedMenuItems = ArrayList<MenuData>()
    private lateinit var rvAdapter: MenuFolderDetailAllRVAdapter

    // 바텀시트 chip 관련
    private lateinit var checkedChipKind: Chip
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var checkedChipCountry: Chip
    private lateinit var checkedChipTaste: Chip
    private lateinit var checkedChipCondition: Chip
    private var tagItems: ArrayList<String?> = arrayListOf(null, null, null, null)
    private var checkChipIndexArray: ArrayList<Int?> = arrayListOf(null, null, null, null) // 체크된 칩들 인덱스
    private var priceRange: MutableList<Float> = arrayListOf(5000f, 50000f)

    private val retrofit = RetrofitObject.retrofit
    private val menuService = retrofit.create(MenuService::class.java)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMenuFolderDetailAllBinding.inflate(layoutInflater)

        initBottomSheet()

        getMenuItems()

        initListener()

//        initSpinner()
//        initRVAdapter()


        return binding.root
    }

    private fun getMenuItems() {
        val tags: ArrayList<String> = tagItems.filterNotNull().toCollection(ArrayList())

        menuService.getMenus(
            tags = tags,
            title = null,
            menuFolderId = null, // 전체 메뉴판일 때에는 null
            page = null,
            size = 100,
            minPrice = priceRange[0].toInt(), maxPrice = priceRange[1].toInt()


        ).enqueue(object : Callback<MenuArrayResponse> {
            override fun onResponse(call: Call<MenuArrayResponse>, response: Response<MenuArrayResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.response?.let {
                        // TODO DiffUtil
                        menuItems.clear()
                        menuItems.addAll(result.response)
                        sortedMenuItems.clear()
                        sortedMenuItems.addAll(result.response)
                        binding.tvMfdaMenuCount.text = menuItems.size.toString()

                        initSpinner()
                        initRVAdapter()
                    }
                }
            }

            override fun onFailure(call: Call<MenuArrayResponse>, t: Throwable) {
                Log.d("AllMenu", t.toString())
            }

        })
    }

    private fun initRVAdapter() {

        rvAdapter =
            MenuFolderDetailAllRVAdapter(menuItems, requireContext(),

                onMenuClick = {
                    val intent = Intent(context, MenuInfoActivity::class.java)
                    intent.putExtra("groupId", it.groupId)
                    intent.putExtra("tag", "menuInfo")
                    startActivity(intent)
                },

                onMapClick = {
                    val intent = Intent(context, MenuInfoActivity::class.java)
                    intent.putExtra("groupId", it.groupId)
                    intent.putExtra("tag", "menuInfoMap")
                    startActivity(intent)
                })
        binding.rvMfdaMenu.adapter = rvAdapter

    }

    private fun initSpinner() {
        val adapter =
            MenuFolderAllFilterSpinnerAdapter<String>(requireContext(), arrayListOf("이름순", "등록순", "가격순"))
        adapter.setDropDownViewResource(R.layout.spinner_item_background)
        binding.spnMfdaFilter.adapter = adapter
        binding.spnMfdaFilter.setSelection(1)
        binding.spnMfdaFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                adapter.selectedPos = position
                sortBySpinner(position)
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
                sortedMenuItems.sortWith(compareBy<MenuData> {
                    val formatter = DateTimeFormatterBuilder()
                        .appendPattern("yyyy-MM-dd'T'HH:mm:ss") // #1
                        .toFormatter()

                    LocalDateTime.parse(it.createdAt, formatter)
                })
            }

            2 -> { // 가격순, 가격이 같다면 이름순
                sortedMenuItems.sortWith(compareBy<MenuData> { it.menuPrice }.thenBy { it.menuTitle })
            }

            else -> return
        }
        rvAdapter.updateList(sortedMenuItems)
        binding.rvMfdaMenu.scrollToPosition(0)


    }

    private fun initListener() {

        binding.ivMfdaBack.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            else
                requireActivity().finish()
        }

        // 기기 뒤로가기
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    requireActivity().finish()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.chipMfdaAll.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.btnMfdaAddMenu.viewGone()
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.mfdaBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val screenHeight = requireContext().resources.displayMetrics.heightPixels
        binding.mfdaBottomSheet.layoutParams.height = (screenHeight * 740) / 800

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.btnMfdaAddMenu.viewVisible()
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.btnMfdaAddMenu.viewVisible()
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
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


        initBottomSheetChips()
        initRangeSlider()
        initBottomSheetListener()
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
                checkedChipKind = copyChip(chip)
                setChipListener(chip, "kind")
                checkChipIndexArray[0] = i
            }
        }

        for (i in 0 until binding.cgMfdaCountry.childCount) {
            val chip = binding.cgMfdaCountry.getChildAt(i) as Chip
            chip.setOnClickListener {
                checkedChipCountry = copyChip(chip)
                setChipListener(chip, "country")
                checkChipIndexArray[1] = i
            }
        }

        for (i in 0 until binding.cgMfdaTaste.childCount) {
            val chip = binding.cgMfdaTaste.getChildAt(i) as Chip
            chip.setOnClickListener {
                checkedChipTaste = copyChip(chip)
                setChipListener(chip, "taste")
                checkChipIndexArray[2] = i
            }
        }


        for (i in 0 until binding.cgMfdaCondition.childCount) {
            val chip = binding.cgMfdaCondition.getChildAt(i) as Chip
            chip.setOnClickListener {
                checkedChipCondition = copyChip(chip)
                setChipListener(chip, "condition")
                checkChipIndexArray[3] = i
            }
        }
    }

    private fun setChipListener(chip: Chip, flag: String) {
        when (flag) {
            "kind" -> {
                checkChipIndexArray[0]?.let {
                    val checkChip = binding.cgMfdaKind.getChildAt(it) as Chip
                    setChipUnSelected(checkChip)
                }
            }

            "country" -> {
                checkChipIndexArray[1]?.let {
                    val checkChip = binding.cgMfdaCountry.getChildAt(it) as Chip
                    setChipUnSelected(checkChip)
                }
            }

            "taste" -> {
                checkChipIndexArray[2]?.let {
                    val checkChip = binding.cgMfdaTaste.getChildAt(it) as Chip
                    setChipUnSelected(checkChip)
                }
            }

            "condition" -> {
                checkChipIndexArray[3]?.let {
                    val checkChip = binding.cgMfdaCondition.getChildAt(it) as Chip
                    setChipUnSelected(checkChip)
                }
            }

            else -> return
        }

        setChipSelected(chip)
    }

    private fun initBottomSheetListener() {

        binding.btnMfdaInitialization.setOnClickListener {
            Log.d("init", binding.btnMfdaInitialization.text.toString())
            // 모두 초기화
            initialChips()
            applyChips()
        }

        binding.btnMfdaApply.setOnClickListener {
            Log.d("apply", binding.btnMfdaApply.text.toString())
            applyChips()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }
    }

    // 초기화 버튼
    private fun initialChips() {
        checkChipIndexArray[0]?.let {
            val checkChip = binding.cgMfdaKind.getChildAt(it) as Chip
            setChipUnSelected(checkChip)
            checkChipIndexArray[0] = null
        }

        checkChipIndexArray[1]?.let {
            val checkChip = binding.cgMfdaCountry.getChildAt(it) as Chip
            setChipUnSelected(checkChip)
            checkChipIndexArray[1] = null
        }

        checkChipIndexArray[2]?.let {
            val checkChip = binding.cgMfdaTaste.getChildAt(it) as Chip
            setChipUnSelected(checkChip)
            checkChipIndexArray[2] = null
        }

        checkChipIndexArray[3]?.let {
            val checkChip = binding.cgMfdaCondition.getChildAt(it) as Chip
            setChipUnSelected(checkChip)
            checkChipIndexArray[3] = null
        }
    }

    // 적용하기
    private fun applyChips() {
        val chipKind = checkChipIndexArray[0]?.let { binding.cgMfdaKind.getChildAt(it) as Chip }

        val chipCountry = checkChipIndexArray[1]?.let { binding.cgMfdaCountry.getChildAt(it) as Chip }

        val chipTaste = checkChipIndexArray[2]?.let { binding.cgMfdaTaste.getChildAt(it) as Chip }

        val chipCondition = checkChipIndexArray[3]?.let { binding.cgMfdaCondition.getChildAt(it) as Chip }

        if (chipKind != null) {
            binding.chipMfdaKind.text = chipKind.text
            binding.chipMfdaKind.chipIcon = chipKind.chipIcon
            tagItems[0] = chipKind.text.toString()
            binding.chipMfdaKind.viewVisible()
        } else {
            tagItems[0] = null
            binding.chipMfdaKind.viewGone()
        }

        if (chipCountry != null) {
            binding.chipMfdaCountry.text = chipCountry.text
            binding.chipMfdaCountry.chipIcon = chipCountry.chipIcon
            tagItems[1] = chipCountry.text.toString()
            binding.chipMfdaCountry.viewVisible()
        } else {
            tagItems[1] = null
            binding.chipMfdaCountry.viewGone()
        }

        if (chipTaste != null) {
            binding.chipMfdaTaste.text = chipTaste.text
            binding.chipMfdaTaste.chipIcon = chipTaste.chipIcon
            tagItems[2] = chipTaste.text.toString()
            binding.chipMfdaTaste.viewVisible()
        } else {
            tagItems[2] = null
            binding.chipMfdaTaste.viewGone()
        }

        if (chipCondition != null) {
            binding.chipMfdaCondition.text = chipCondition.text
            binding.chipMfdaCondition.chipIcon = chipCondition.chipIcon
            tagItems[3] = chipCondition.text.toString()
            binding.chipMfdaCondition.viewVisible()
        } else {
            tagItems[3] = null
            binding.chipMfdaCondition.viewGone()
        }

        getMenuItems()
    }

    @SuppressLint("SetTextI18n")
    private fun initRangeSlider() {
        // TODO 회의 후 자세한 수치 조정

        binding.rsMfdaRangeSlider.run {
            valueFrom = 5000f // valueFrom , valueTo : 슬라이더가 가질 수 있는 총 범위
            valueTo = 50000f
            setValues(5000f, 50000f) // 슬라이더가 시작할 초기 범위
            stepSize = 1000f // 슬라이더 간격 사이즈
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
            priceRange = values

            binding.tvMfdaStartPrice.text = if (values[0] == 5000f) {
                toWon(values[0]) + " 이하"
            } else {
                toWon(values[0])
            }
            binding.tvMfdaEndPrice.text = if (values[1] == 50000f) {
                toWon(values[1]) + " 이상"
            } else {
                toWon(values[1])
            }
        })
    }

    private fun copyChip(oldChip: Chip): Chip {
        val newChip = Chip(requireContext()).apply {
            // TODO chip icon 추가
            text = oldChip.text
            chipIcon = oldChip.chipIcon // 아이콘 복제
        }
        return newChip
    }

    private fun setChipSelected(chip: Chip) {
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.Primary_500_main)
        chip.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White))
    }

    private fun setChipUnSelected(chip: Chip) {
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
        chip.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black))
    }

}
