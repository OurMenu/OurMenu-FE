package com.example.ourmenu.addMenu.bottomSheetClass

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.AddMenuTagFragment
import com.example.ourmenu.databinding.AddMenuBottomSheetIconBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// 바텀시트 프래그먼트를 따로 다이얼로그로 구현한 클래스. 바텀시트 여러 개가 include로 사용되지 않아서 이 방법으로 구현함.
// 바텀시트 객체 생성할때 부모 프래그먼트와 마지막으로 선택된 아이템의 위치를 받음.
class AddMenuBottomSheetIcon(
    val fragment: AddMenuTagFragment,
    var selected: Int,
) : BottomSheetDialogFragment() {
    lateinit var binding: AddMenuBottomSheetIconBinding
    lateinit var selectList: ArrayList<View> // 아이콘 선택되었을시 보이는 회색 동그라미들 모음
    lateinit var currentSelected: View // 최근 선택된 아이콘의 회색 동그라미
    var currentSelectedIcon = 0 // 최근 선택된 아이콘의 src id
    lateinit var selectedIconList: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        this.setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val behavior = this.view?.let { BottomSheetBehavior.from(it) }
        behavior?.isFitToContents = false // 내용에 맞게 크기를 조정하지 않도록 설정
        binding = AddMenuBottomSheetIconBinding.inflate(inflater, container, false)
        binding.root.setBackgroundResource(R.drawable.bottom_sheet_bg)
        this.dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selectList =
            arrayListOf(
                binding.ivAmbsiAsianSelected,
                binding.ivAmbsiBreadSelected,
                binding.ivAmbsiBunsikSelected,
                binding.ivAmbsiCoffeeSelected,
                binding.ivAmbsiChickenSelected,
                binding.ivAmbsiChinaSelected,
                binding.ivAmbsiFastfoodSelected,
                binding.ivAmbsiCakeSelected,
                binding.ivAmbsiFishSelected,
                binding.ivAmbsiFriedSelected,
                binding.ivAmbsiJapanSelected,
                binding.ivAmbsiJjigaeSelected,
                binding.ivAmbsiKoreaSelected,
                binding.ivAmbsiMeatSelected,
                binding.ivAmbsiPizzaSelected,
                binding.ivAmbsiRiceSelected,
                binding.ivAmbsiSushiSelected,
                binding.ivAmbsiNoodleSelected,
                binding.ivAmbsiWestSelected
            )
        selectedIconList =
            arrayListOf(
                R.drawable.icon_asian_white,
                R.drawable.icon_bread_white,
                R.drawable.icon_bunsik_white,
                R.drawable.icon_cafe_white,
                R.drawable.icon_chicken_white,
                R.drawable.icon_china_white,
                R.drawable.icon_fastfood_white,
                R.drawable.icon_cake_white,
                R.drawable.icon_fish_white,
                R.drawable.icon_fried_white,
                R.drawable.icon_japan_white,
                R.drawable.icon_jjigae_white,
                R.drawable.icon_korea_white,
                R.drawable.icon_meat_white,
                R.drawable.icon_pizza_white,
                R.drawable.icon_rice_white,
                R.drawable.icon_sushi_white,
                R.drawable.icon_noodle_white,
                R.drawable.icon_west_white
            )
        currentSelectedIcon = selectedIconList[selected]
        // 아이콘프레임 클릭시 선택된 상태가 아니라면 선택된 상태로 변경
        binding.flAmbsiAsian.setOnClickListener {
            whenClick(0, binding.flAmbsiAsian)
        }
        binding.flAmbsiBread.setOnClickListener {
            whenClick(1, binding.flAmbsiBread)
        }
        binding.flAmbsiBunsik.setOnClickListener {
            whenClick(2, binding.flAmbsiBunsik)
        }
        binding.flAmbsiCoffee.setOnClickListener {
            whenClick(3, binding.flAmbsiCoffee)
        }
        binding.flAmbsiChicken.setOnClickListener {
            whenClick(4, binding.flAmbsiChicken)
        }
        binding.flAmbsiChina.setOnClickListener {
            whenClick(5, binding.flAmbsiChina)
        }
        binding.flAmbsiFastfood.setOnClickListener {
            whenClick(6, binding.flAmbsiFastfood)
        }
        binding.flAmbsiCake.setOnClickListener {
            whenClick(7, binding.flAmbsiCake)
        }
        binding.flAmbsiFish.setOnClickListener {
            whenClick(8, binding.flAmbsiFish)
        }
        binding.flAmbsiFried.setOnClickListener {
            whenClick(9, binding.flAmbsiFried)
        }
        binding.flAmbsiJapan.setOnClickListener {
            whenClick(10, binding.flAmbsiJapan)
        }
        binding.flAmbsiJjigae.setOnClickListener {
            whenClick(11, binding.flAmbsiJjigae)
        }
        binding.flAmbsiKorea.setOnClickListener {
            whenClick(12,binding.flAmbsiKorea)
        }
        binding.flAmbsiMeat.setOnClickListener {
            whenClick(13,binding.flAmbsiMeat)
        }
        binding.flAmbsiPizza.setOnClickListener {
            whenClick(14,binding.flAmbsiPizza)
        }
        binding.flAmbsiRice.setOnClickListener {
            whenClick(15,binding.flAmbsiRice)
        }
        binding.flAmbsiSushi.setOnClickListener {
            whenClick(16,binding.flAmbsiSushi)
        }
        binding.flAmbsiNoodle.setOnClickListener {
            whenClick(17,binding.flAmbsiNoodle)
        }
        binding.flAmbsiWest.setOnClickListener {
            whenClick(18,binding.flAmbsiWest)
        }
        // 바텀시트에서 선택된 내용을 fragment로 전달
        binding.btnAmbstConfirm.setOnClickListener {
            fragment.binding.ivAddMenuTagIcon.setImageResource(currentSelectedIcon)
            fragment.bottomSheetIconStart = selected
            fragment.updateMenuIconType(selected) // 추가된 부분
            this.dialog?.dismiss()
        }
        // 취소시 그냥 화면만 없앰
        binding.btnAmbstReset.setOnClickListener {
            this.dialog?.dismiss()
        }
        currentSelected = selectList[selected]
        currentSelected.visibility = View.VISIBLE

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            this.window?.setBackgroundDrawable(null)
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        // 화면 사라질때 부모의 블러 제거
        dialog?.setOnDismissListener {
            fragment.clearBlur()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    fun whenClick(index: Int, view: View) {
        if (selectList[index].visibility != View.VISIBLE) {
            selected = index
            currentSelectedIcon = selectedIconList[index]
            currentSelected.visibility = View.INVISIBLE
            selected = binding.cgAmbsiIcon.indexOfChild(view)
            currentSelected = selectList[selected]
            currentSelected.visibility = View.VISIBLE
        }
    }
}
