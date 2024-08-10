package com.example.ourmenu.menu.menuFolder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.example.ourmenu.R
import com.example.ourmenu.databinding.FragmentMenuFolderDetailAllFilterBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import java.text.NumberFormat
import java.util.Locale

class MenuFolderDetailAllFilterFragment(val menuFolderDetailAllFragment: MenuFolderDetailAllFragment) : Fragment() {

    lateinit var binding: FragmentMenuFolderDetailAllFilterBinding
    lateinit var checkedChipKind: Chip
    lateinit var checkedChipCountry: Chip
    lateinit var checkedChipTaste: Chip
    lateinit var checkedChipCondition: Chip
    private var priceRange: ArrayList<Int> = arrayListOf(0, 0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMenuFolderDetailAllFilterBinding.inflate(layoutInflater)

        initBottomSheetChips()
        initRangeSlider()
        initBottomSheetListener()

        return binding.root
    }

    private fun initBottomSheetListener() {
//        binding.ivMfdafBack.setOnClickListener {
//            parentFragmentManager.popBackStack()
//        }

        binding.btnMfdalInitialization.setOnClickListener {
            // 모두 초기화
            initBottomSheetChips()
        }

        binding.btnMfdalApply.setOnClickListener {
            val chips = ArrayList<Chip>()

            if (isCheckedChip(checkedChipKind))
                chips.add(checkedChipKind)
            if (isCheckedChip(checkedChipCountry))
                chips.add(checkedChipCountry)
            if (isCheckedChip(checkedChipTaste))
                chips.add(checkedChipTaste)
            if (isCheckedChip(checkedChipCondition))
                chips.add(checkedChipCondition)

//            menuFolderDetailAllFragment.addChips(requireContext(), chips)
            menuFolderDetailAllFragment.arguments = bundleOf(
                Pair<String, String>(
                    "start", binding.tvMfdafStartPrice.text.toString()
                )
            )

            menuFolderDetailAllFragment.arguments = bundleOf(
                Pair<String, String>(
                    "end", binding.tvMfdafEndPrice.text.toString()
                )
            )
            parentFragmentManager.popBackStack()
        }
    }

    // 체크되어있으면 true 리턴
    private fun isCheckedChip(chip: Chip): Boolean {
        return chip.chipBackgroundColor ==
            ContextCompat.getColorStateList(requireContext(), R.color.Primary_500_main)
    }

    // 기존 선택된 chip 을 초기화하고, 새로운걸 선택된 것 처럼 표시하고 선택된 것들을 저장함
    // 1개만 선택가능.
    private fun initBottomSheetChips() {
        // 기본값으로 초기화
        checkedChipKind = Chip(requireContext())
        checkedChipCountry = Chip(requireContext())
        checkedChipTaste = Chip(requireContext())
        checkedChipCondition = Chip(requireContext())

        val oldChips = arguments ?: Bundle()

        for (i in 0 until binding.cgMfdafKind.childCount) {
            val chip = binding.cgMfdafKind.getChildAt(i) as Chip
            if (initChip(chip, oldChips)) {
                checkedChipKind = setChipSelected(chip, 1)
            }
            chip.setOnClickListener {
                // 선택된 상태
                checkedChipKind = setChipSelected(chip, 1)
            }
        }

        for (i in 0 until binding.cgMfdafCountry.childCount) {
            val chip = binding.cgMfdafCountry.getChildAt(i) as Chip
            if (initChip(chip, oldChips)) {
                checkedChipCountry = setChipSelected(chip, 2)
            }
            chip.setOnClickListener {
                // 선택된 상태
                checkedChipCountry = setChipSelected(chip, 2)
            }
        }

        for (i in 0 until binding.cgMfdafTaste.childCount) {
            val chip = binding.cgMfdafTaste.getChildAt(i) as Chip
            if (initChip(chip, oldChips)) {
                checkedChipTaste = setChipSelected(chip, 3)
            }
            chip.setOnClickListener {
                // 선택된 상태
                checkedChipTaste = setChipSelected(chip, 3)
            }
        }


        for (i in 0 until binding.cgMfdafCondition.childCount) {
            val chip = binding.cgMfdafCondition.getChildAt(i) as Chip
            if (initChip(chip, oldChips)) {
                checkedChipCondition = setChipSelected(chip, 4)
            }
            chip.setOnClickListener {
                // 선택된 상태
                checkedChipCondition = setChipSelected(chip, 4)
            }
        }
    }

    // 이전에 선택된 칩인지 체크
    private fun initChip(chip: Chip, oldChips: Bundle): Boolean {
        var flag = false
        for (key in oldChips.keySet()) {
            val chipText = oldChips.getString(key)
            if (chipText == chip.text.toString()) {
                flag = true
            }
        }
        return flag
    }


    private fun setChipSelected(newChip: Chip, flag: Int): Chip {
        newChip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.Primary_500_main)
        newChip.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
        newChip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White))

        when (flag) {
            1 -> {
                checkedChipKind.chipBackgroundColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
                checkedChipKind.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
                checkedChipKind.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black))

                checkedChipKind = newChip
            }

            2 -> {
                checkedChipCountry.chipBackgroundColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
                checkedChipCountry.chipIconTint =
                    ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
                checkedChipCountry.setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.Neutral_Black
                    )
                )

                checkedChipCountry = newChip
            }

            3 -> {
                checkedChipTaste.chipBackgroundColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
                checkedChipTaste.chipIconTint = ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
                checkedChipTaste.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black))

                checkedChipTaste = newChip
            }

            else -> {
                checkedChipCondition.chipBackgroundColor =
                    ContextCompat.getColorStateList(requireContext(), R.color.Neutral_White)
                checkedChipCondition.chipIconTint =
                    ContextCompat.getColorStateList(requireContext(), R.color.Neutral_Black)
                checkedChipCondition.setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.Neutral_Black
                    )
                )

                checkedChipCondition = newChip
            }
        }

        return newChip
    }


    @SuppressLint("SetTextI18n")
    private fun initRangeSlider() {
        // TODO 회의 후 자세한 수치 조정

        binding.rsMfdafRangeSlider.run {
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

        binding.rsMfdafRangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
                Log.d("start", "start")
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                Log.d("stop", "stop")

            }
        })

        binding.rsMfdafRangeSlider.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            val values = slider.values
            val wonFormat = NumberFormat.getNumberInstance(Locale("ko", "KR"))
            wonFormat.maximumFractionDigits = 0
            wonFormat.minimumFractionDigits = 0
            binding.tvMfdafStartPrice.text = "${wonFormat.format(values[0] * 1000)}원"
            binding.tvMfdafEndPrice.text = "${wonFormat.format(values[1] * 1000)}원"
        })

    }
}
