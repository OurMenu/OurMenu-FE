package com.example.ourmenu.addMenu.bottomSheetClass

import android.app.Dialog
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
                binding.ivAmbsiWatermelonSelected,
                binding.ivAmbsiStrawberrySelected,
                binding.ivAmbsiIcecreamSelected,
                binding.ivAmbsiDonutSelected,
                binding.ivAmbsiHotdogStickSelected,
                binding.ivAmbsiHamburgerSelected,
                binding.ivAmbsiCupcakeSelected,
                binding.ivAmbsiPizzaSelected,
                binding.ivAmbsiChickenSelected,
                binding.ivAmbsiHotdogSelected,
                binding.ivAmbsiCoffeeSelected,
            )
        // 아이콘프레임 클릭시 선택된 상태가 아니라면 선택된 상태로 변경
        binding.flAmbsiWatermelon.setOnClickListener {
            if (binding.ivAmbsiWatermelonSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiWatermelon)
                currentSelected = selectList[selected]
                currentSelectedIcon = R.drawable.ic_watermelon
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiChicken.setOnClickListener {
            if (binding.ivAmbsiChickenSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_chicken
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiChicken)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiCoffee.setOnClickListener {
            if (binding.ivAmbsiCoffeeSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_coffee
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiCoffee)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiCupcake.setOnClickListener {
            if (binding.ivAmbsiCupcakeSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_cupcake
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiCupcake)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiDonut.setOnClickListener {
            if (binding.ivAmbsiDonutSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_donut
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiDonut)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiHamburger.setOnClickListener {
            if (binding.ivAmbsiHamburgerSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_hamburger
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiHamburger)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiHotdog.setOnClickListener {
            if (binding.ivAmbsiHotdogSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_hotdog
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiHotdog)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiHotdogStick.setOnClickListener {
            if (binding.ivAmbsiHotdogStickSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_hotdog_stick
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiHotdogStick)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiIcecream.setOnClickListener {
            if (binding.ivAmbsiIcecreamSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_icecream
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiIcecream)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiPizza.setOnClickListener {
            if (binding.ivAmbsiPizzaSelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_pizza
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiPizza)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        binding.flAmbsiStrawberry.setOnClickListener {
            if (binding.ivAmbsiStrawberrySelected.visibility != View.VISIBLE) {
                currentSelected.visibility = View.INVISIBLE
                currentSelectedIcon = R.drawable.ic_strawberry
                selected = binding.cgAmbsiIcon.indexOfChild(binding.flAmbsiStrawberry)
                currentSelected = selectList[selected]
                currentSelected.visibility = View.VISIBLE
            }
        }
        // 바텀시트에서 선택된 내용을 fragment로 전달
        binding.btnAmbstConfirm.setOnClickListener {
            fragment.binding.ivAddMenuTagIcon.setImageResource(currentSelectedIcon)
            fragment.bottomSheetIconStart = selected
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
}
