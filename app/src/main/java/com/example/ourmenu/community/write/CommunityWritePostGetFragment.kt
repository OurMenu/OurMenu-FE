package com.example.ourmenu.community.write

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.ourmenu.R
import com.example.ourmenu.data.community.ArticleRequestData
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.menu.response.MenuArrayResponse
import com.example.ourmenu.databinding.FragmentCommunityWritePostGetBinding
import com.example.ourmenu.menu.adapter.MenuFolderAllFilterSpinnerAdapter
import com.example.ourmenu.menu.menuFolder.post.adapter.CommunityWritePostGetRVAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuService
import com.example.ourmenu.util.Utils.getTypeOf
import com.example.ourmenu.util.Utils.toWon
import com.example.ourmenu.util.Utils.viewGone
import com.example.ourmenu.util.Utils.viewVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityWritePostGetFragment : Fragment() {
    lateinit var binding: FragmentCommunityWritePostGetBinding
    lateinit var rvAdapter: CommunityWritePostGetRVAdapter

    //    lateinit var dummyItems: ArrayList<MenuData>
    private val menuItems = ArrayList<MenuData>()
    private val sortedMenuItems = ArrayList<MenuData>()

    // 바텀시트 chip 관련
    private lateinit var checkedChipKind: Chip
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var checkedChipCountry: Chip
    private lateinit var checkedChipTaste: Chip
    private lateinit var checkedChipCondition: Chip
    private var tagItems: ArrayList<String?> = arrayListOf(null, null, null, null)
    private var checkChipIndexArray: ArrayList<Int?> = arrayListOf(null, null, null, null) // 체크된 칩들 인덱스
    private var priceRange: MutableList<Float> = arrayListOf(0f, 0f)

    private val retrofit = RetrofitObject.retrofit
    private val menuService = retrofit.create(MenuService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCommunityWritePostGetBinding.inflate(layoutInflater)

//        initSpinner()
        initBottomSheet()

//        initDummy()
        getMenuItems()

        initListener()
//        initRV()

        return binding.root
    }

    private fun getMenuItems() {
        val tags: ArrayList<String> = tagItems.filterNotNull().toCollection(ArrayList())

        menuService
            .getMenus(
                tags = tags,
                title = null,
                menuFolderId = null, // 전체 메뉴판일 때에는 null
                page = null,
                size = 1000,
                minPrice = 5000,
                maxPrice = 50000,
            ).enqueue(
                object : Callback<MenuArrayResponse> {
                    override fun onResponse(
                        call: Call<MenuArrayResponse>,
                        response: Response<MenuArrayResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            result?.response?.let {
                                // TODO DiffUtil
                                menuItems.clear()
                                menuItems.addAll(result.response)
                                sortedMenuItems.clear()
                                sortedMenuItems.addAll(result.response)
                                binding.tvCwpgMenuCount.text = menuItems.size.toString()

                                initSpinner()
                                initRV()
                                checkButtonEnabled()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<MenuArrayResponse>,
                        t: Throwable,
                    ) {
                        Log.d("AllMenu", t.toString())
                    }
                },
            )
    }

    private fun initRV() {
        rvAdapter =
            CommunityWritePostGetRVAdapter(menuItems, requireContext()).apply {
                setOnItemClickListener {
                    checkButtonEnabled()
                }
            }
        binding.rvCwpgMenu.adapter = rvAdapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCwpgMenu)
    }

    private fun initSpinner() {
        val adapter =
            MenuFolderAllFilterSpinnerAdapter<String>(requireContext(), arrayListOf("이름순", "등록순", "가격순"))
        adapter.setDropDownViewResource(R.layout.spinner_item_background)
        binding.spnCwpgFilter.adapter = adapter
        binding.spnCwpgFilter.setSelection(1)
        binding.spnCwpgFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
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
                sortedMenuItems.sortWith(compareBy<MenuData> { it.menuTitle }.thenBy { it.menuPrice })
            }

            2 -> { // 가격순, 가격이 같다면 이름순
                sortedMenuItems.sortWith(compareBy<MenuData> { it.menuPrice }.thenBy { it.menuTitle })
            }

            else -> return
        }
        rvAdapter.updateList(sortedMenuItems)
        binding.rvCwpgMenu.scrollToPosition(0)
    }

    private fun initListener() {
        // 뒤로가기
        binding.ivCwpgBack.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                parentFragmentManager.popBackStack()
            }
        }

        // 기기 뒤로가기
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    } else {
                        parentFragmentManager.popBackStack()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btnCwpgAddMenu.setOnClickListener {
            val bundle = Bundle()

            // 이전에 추가했던것
            val items =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arguments?.getSerializable("items", getTypeOf<ArrayList<ArticleRequestData>>())
                        ?: arrayListOf()
                } else {
                    arguments?.getSerializable("items") as ArrayList<ArticleRequestData>
                        ?: arrayListOf()
                } // 제네릭으로 * 을 줘야 getSerializable 가능

            Log.d("nul", rvAdapter.checkedItems.toString())
            items.addAll(
                rvAdapter.checkedItems
                    .map {
                        val menuUrl = if (it.menuImgUrl.isNullOrEmpty()) "" else it.menuImgUrl

                        ArticleRequestData(
                            placeTitle = it.placeTitle,
                            menuTitle = it.menuTitle,
                            menuPrice = it.menuPrice,
                            menuImgUrl = menuUrl,
                            menuAddress = it.placeAddress,
                            "",
                            "",
                            "",
                            0,
                            0,
                        )
                    }.toCollection(ArrayList()),
            )

            val title = arguments?.getString("title")
//            Log.d("tt", title)
            val content = arguments?.getString("content")

            bundle.putString("title", title)
            bundle.putString("content", content)
            bundle.putSerializable("items", items)

            val communityWritePostFragment = CommunityWritePostFragment()
            communityWritePostFragment.arguments = bundle

            with(parentFragmentManager) {
                popBackStack()
                beginTransaction()
                    .replace(R.id.community_post_frm, communityWritePostFragment)
                    .commit()
            }
        }

        binding.chipCwpgAll.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun checkButtonEnabled() {
        // 체크된 항목이 없으면 비활성화,
        // 체크된 항목이 있으면 활성화
        binding.btnCwpgAddMenu.isEnabled = rvAdapter.checkedItems.isNotEmpty()
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.cwpgBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val screenHeight = requireContext().resources.displayMetrics.heightPixels
        binding.cwpgBottomSheet.layoutParams.height = (screenHeight * 740) / 800

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int,
                ) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.btnCwpgAddMenu.viewVisible()
                        }

                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            binding.btnCwpgAddMenu.viewVisible()
                        }

                        BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.btnCwpgAddMenu.viewGone()
                        }

                        else -> {
                            return
                        }
                    }
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float,
                ) {
                }
            },
        )

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

        for (i in 0 until binding.cgCwpgKind.childCount) {
            val chip = binding.cgCwpgKind.getChildAt(i) as Chip
            chip.setOnClickListener {
                checkedChipKind = copyChip(chip)
                setChipListener(chip, "kind")
                checkChipIndexArray[0] = i
            }
        }

        for (i in 0 until binding.cgCwpgCountry.childCount) {
            val chip = binding.cgCwpgCountry.getChildAt(i) as Chip
            chip.setOnClickListener {
                checkedChipCountry = copyChip(chip)
                setChipListener(chip, "country")
                checkChipIndexArray[1] = i
            }
        }

        for (i in 0 until binding.cgCwpgTaste.childCount) {
            val chip = binding.cgCwpgTaste.getChildAt(i) as Chip
            chip.setOnClickListener {
                checkedChipTaste = copyChip(chip)
                setChipListener(chip, "taste")
                checkChipIndexArray[2] = i
            }
        }

        for (i in 0 until binding.cgCwpgCondition.childCount) {
            val chip = binding.cgCwpgCondition.getChildAt(i) as Chip
            chip.setOnClickListener {
                checkedChipCondition = copyChip(chip)
                setChipListener(chip, "condition")
                checkChipIndexArray[3] = i
            }
        }
    }

    private fun setChipListener(
        chip: Chip,
        flag: String,
    ) {
        when (flag) {
            "kind" -> {
                checkChipIndexArray[0]?.let {
                    val checkChip = binding.cgCwpgKind.getChildAt(it) as Chip
                    setChipUnSelected(checkChip)
                }
            }

            "country" -> {
                checkChipIndexArray[1]?.let {
                    val checkChip = binding.cgCwpgCountry.getChildAt(it) as Chip
                    setChipUnSelected(checkChip)
                }
            }

            "taste" -> {
                checkChipIndexArray[2]?.let {
                    val checkChip = binding.cgCwpgTaste.getChildAt(it) as Chip
                    setChipUnSelected(checkChip)
                }
            }

            "condition" -> {
                checkChipIndexArray[3]?.let {
                    val checkChip = binding.cgCwpgCondition.getChildAt(it) as Chip
                    setChipUnSelected(checkChip)
                }
            }

            else -> return
        }

        setChipSelected(chip)
    }

    private fun initBottomSheetListener() {
        binding.btnCwpgInitialization.setOnClickListener {
            Log.d("init", binding.btnCwpgInitialization.text.toString())
            // 모두 초기화
            initialChips()
        }

        binding.btnCwpgApply.setOnClickListener {
            Log.d("apply", binding.btnCwpgApply.text.toString())
            applyChips()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    // 초기화 버튼
    private fun initialChips() {
        checkChipIndexArray[0]?.let {
            val checkChip = binding.cgCwpgKind.getChildAt(it) as Chip
            setChipUnSelected(checkChip)
            checkChipIndexArray[0] = null
        }

        checkChipIndexArray[1]?.let {
            val checkChip = binding.cgCwpgCountry.getChildAt(it) as Chip
            setChipUnSelected(checkChip)
            checkChipIndexArray[1] = null
        }

        checkChipIndexArray[2]?.let {
            val checkChip = binding.cgCwpgTaste.getChildAt(it) as Chip
            setChipUnSelected(checkChip)
            checkChipIndexArray[2] = null
        }

        checkChipIndexArray[3]?.let {
            val checkChip = binding.cgCwpgCondition.getChildAt(it) as Chip
            setChipUnSelected(checkChip)
            checkChipIndexArray[3] = null
        }
    }

    // 적용하기
    private fun applyChips() {
        val chipKind = checkChipIndexArray[0]?.let { binding.cgCwpgKind.getChildAt(it) as Chip }

        val chipCountry = checkChipIndexArray[1]?.let { binding.cgCwpgCountry.getChildAt(it) as Chip }

        val chipTaste = checkChipIndexArray[2]?.let { binding.cgCwpgTaste.getChildAt(it) as Chip }

        val chipCondition = checkChipIndexArray[3]?.let { binding.cgCwpgCondition.getChildAt(it) as Chip }

        if (chipKind != null) {
            binding.chipCwpgKind.text = chipKind.text
            binding.chipCwpgKind.chipIcon = chipKind.chipIcon
            tagItems[0] = chipKind.text.toString()
            binding.chipCwpgKind.viewVisible()
        } else {
            tagItems[0] = null
            binding.chipCwpgKind.viewGone()
        }

        if (chipCountry != null) {
            binding.chipCwpgCountry.text = chipCountry.text
            binding.chipCwpgCountry.chipIcon = chipCountry.chipIcon
            tagItems[1] = chipCountry.text.toString()
            binding.chipCwpgCountry.viewVisible()
        } else {
            tagItems[1] = null
            binding.chipCwpgCountry.viewGone()
        }

        if (chipTaste != null) {
            binding.chipCwpgTaste.text = chipTaste.text
            binding.chipCwpgTaste.chipIcon = chipTaste.chipIcon
            tagItems[2] = chipTaste.text.toString()
            binding.chipCwpgTaste.viewVisible()
        } else {
            tagItems[2] = null
            binding.chipCwpgTaste.viewGone()
        }

        if (chipCondition != null) {
            binding.chipCwpgCondition.text = chipCondition.text
            binding.chipCwpgCondition.chipIcon = chipCondition.chipIcon
            tagItems[3] = chipCondition.text.toString()
            binding.chipCwpgCondition.viewVisible()
        } else {
            tagItems[3] = null
            binding.chipCwpgCondition.viewGone()
        }

        getMenuItems()
    }

    @SuppressLint("SetTextI18n")
    private fun initRangeSlider() {
        // TODO 회의 후 자세한 수치 조정

        binding.rsCwpgRangeSlider.run {
            valueFrom = 5000f // valueFrom , valueTo : 슬라이더가 가질 수 있는 총 범위
            valueTo = 50000f
            setValues(5000f, 50000f) // 슬라이더가 시작할 초기 범위
            stepSize = 1000f // 슬라이더 간격 사이즈
            setCustomThumbDrawablesForValues(
                R.drawable.ic_slider_thumb_left,
                R.drawable.ic_slider_thumb_right,
            ) // thumb 설정
            trackActiveTintList = ContextCompat.getColorStateList(requireContext(), R.color.Primary_500_main)!!
            // 활성화 색상
            trackInactiveTintList = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_300)!!
            // 비활성화 색상
            labelBehavior = LabelFormatter.LABEL_GONE
            // 라벨 없애기
        }

        binding.rsCwpgRangeSlider.addOnSliderTouchListener(
            object : RangeSlider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: RangeSlider) {
                    Log.d("start", "start")
                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    Log.d("stop", "stop")
                }
            },
        )

        binding.rsCwpgRangeSlider.addOnChangeListener(
            RangeSlider.OnChangeListener { slider, value, fromUser ->
                val values = slider.values
                priceRange = values

                binding.tvCwpgStartPrice.text =
                    if (values[0] == 5000f) {
                        toWon(values[0]) + " 이하"
                    } else {
                        toWon(values[0])
                    }
                binding.tvCwpgEndPrice.text =
                    if (values[1] == 50000f) {
                        toWon(values[1]) + " 이상"
                    } else {
                        toWon(values[1])
                    }
            },
        )
    }

    private fun copyChip(oldChip: Chip): Chip {
        val newChip =
            Chip(requireContext()).apply {
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
