package com.example.ourmenu.addMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.adapter.AddMenuPlaceMenuRVAdapter
import com.example.ourmenu.data.PlaceInfoData
import com.example.ourmenu.data.PlaceMenuData
import com.example.ourmenu.databinding.FragmentAddMenuSelectMenuBinding

class AddMenuSelectMenuFragment : Fragment() {
    lateinit var binding: FragmentAddMenuSelectMenuBinding

    lateinit var menuAdapter: AddMenuPlaceMenuRVAdapter

    private lateinit var placeItems: ArrayList<PlaceInfoData>
    private lateinit var placeMenuItems: ArrayList<PlaceMenuData>
    private lateinit var placeMenuImgItems: ArrayList<Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddMenuSelectMenuBinding.inflate(inflater, container, false)

        initDummyData()
        initMenuRV()

        // 뒤로가기 버튼 클릭 이벤트 처리
        binding.ivAmsmLogoBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 다음 버튼 클릭 이벤트 처리
        binding.btnAmsmNext.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .addToBackStack("AddMenuSelectMenu")
                .replace(R.id.cl_add_menu_main, AddMenuNameFragment())
                .commit()
        }

        return binding.root
    }

    private fun initMenuRV() {
        menuAdapter =
            AddMenuPlaceMenuRVAdapter(
                placeMenuItems,
                onItemSelected = { selectedPosition ->
                    // 아이템이 선택되었을 때 버튼을 활성화, 선택이 취소되면 비활성화
                    binding.btnAmsmNext.isEnabled = selectedPosition != null
                },
                onButtonClicked = {
                    parentFragmentManager
                        .beginTransaction()
                        .addToBackStack("AddMenuSelectMenu")
                        .replace(R.id.cl_add_menu_main, AddMenuNameFragment())
                        .commit()
                },
            )

        binding.rvAmsmPlaceMenu.layoutManager = LinearLayoutManager(context)
        binding.rvAmsmPlaceMenu.adapter = menuAdapter

        // 버튼 초기 상태를 비활성화
        binding.btnAmsmNext.isEnabled = false
    }

    private fun initDummyData() {
        placeMenuImgItems = arrayListOf(R.drawable.menu_sample, R.drawable.menu_sample2, R.drawable.menu_sample3)

        placeMenuItems =
            arrayListOf(
                PlaceMenuData("마라탕", "14,000원"),
                PlaceMenuData("마라샹궈", "20,000원"),
                PlaceMenuData("꿔바로우", "12,000원"),
                PlaceMenuData("일반 떡볶이", "14,000원"),
                PlaceMenuData("로제 떡볶이", "15,000원"),
                PlaceMenuData("짜장 떡볶이", "13,000원"),
                PlaceMenuData("야채김밥", "4,000원"),
                PlaceMenuData("참치김밥", "5,000원"),
                PlaceMenuData("김치김밥", "5,000원"),
            )

        placeItems =
            arrayListOf(
                PlaceInfoData(
                    "건대 마라",
                    "음식점",
                    "10km",
                    true,
                    "서울 마포구 와우산로 112 1",
                    "매일 10:00 - 21:00",
                    placeMenuImgItems,
                    placeMenuItems,
                    "1270127015",
                    "375705633",
                ),
                PlaceInfoData(
                    "아워 떡볶이",
                    "음식점",
                    "8km",
                    false,
                    "서울 마포구 와우산로 112 2",
                    "매일 10:00 - 21:00",
                    placeMenuImgItems,
                    placeMenuItems,
                    "1270901844",
                    "375537588",
                ),
                PlaceInfoData(
                    "쿠잇 분식점",
                    "음식점",
                    "7km",
                    true,
                    "서울 마포구 와우산로 112 3",
                    "매일 10:00 - 21:00",
                    placeMenuImgItems,
                    placeMenuItems,
                    "1270716909",
                    "375426950",
                ),
            )
    }
}
