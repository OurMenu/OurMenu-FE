package com.example.ourmenu.addMenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.adapter.AddMenuPlaceMenuRVAdapter
import com.example.ourmenu.data.place.PlaceDetailData
import com.example.ourmenu.data.place.PlaceDetailMenuData
import com.example.ourmenu.data.place.PlaceDetailResponse
import com.example.ourmenu.databinding.FragmentAddMenuSelectMenuBinding
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.PlaceService
import retrofit2.Call
import retrofit2.Response

class AddMenuSelectMenuFragment : Fragment() {
    lateinit var binding: FragmentAddMenuSelectMenuBinding

    lateinit var menuAdapter: AddMenuPlaceMenuRVAdapter

    private lateinit var placeMenuItems: ArrayList<PlaceDetailMenuData>

    private lateinit var placeDetailItem: PlaceDetailData
    private var selectedMenuItem: PlaceDetailMenuData? = null // 선택된 메뉴 아이템을 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddMenuSelectMenuBinding.inflate(inflater, container, false)

        // ID를 전달받아 fetchPlaceDetail 호출
        arguments?.getString("PLACE_ID")?.let { placeId ->
            fetchPlaceDetail(placeId)
        }

        initMenuRV()

        // 뒤로가기 버튼 클릭 이벤트 처리
        binding.ivAmsmLogoBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 다음 버튼 클릭 이벤트 처리
        binding.btnAmsmNext.setOnClickListener {
            selectedMenuItem?.let { selectedItem ->
                val bundle =
                    Bundle().apply {
                        putString("MENU_NAME", selectedItem.menuTitle)
                        putString("MENU_PRICE", selectedItem.menuPrice)
                        putString("PLACE_NAME", placeDetailItem.placeTitle)
                        putString("PLACE_ADDRESS", placeDetailItem.placeAddress)
                        putString("PLACE_TIME", placeDetailItem.timeInfo)
                    }

                val fragment =
                    AddMenuNameFragment().apply {
                        arguments = bundle
                    }

                parentFragmentManager
                    .beginTransaction()
                    .addToBackStack("AddMenuSelectMenu")
                    .replace(R.id.cl_add_menu_main, fragment)
                    .commit()
            }
        }

        return binding.root
    }

    private fun fetchPlaceDetail(id: String) {
        val service = RetrofitObject.retrofit.create(PlaceService::class.java)
        val call = service.getPlaceInfoDetail(id)

        call.enqueue(
            object : retrofit2.Callback<PlaceDetailResponse> {
                override fun onResponse(
                    call: Call<PlaceDetailResponse>,
                    response: Response<PlaceDetailResponse>,
                ) {
                    if (response.isSuccessful) {
                        val placeDetailResponse = response.body()

                        if (placeDetailResponse?.isSuccess == true) {
                            placeDetailItem = placeDetailResponse.response
                            showPlaceDetails(placeDetailItem) // 데이터가 성공적으로 받아졌을 때 UI 업데이트
                        } else {
                            val errorMessage = placeDetailResponse?.errorResponse?.message ?: "error"
                            Log.d("오류1", errorMessage)
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        Log.d("오류2", errorResponse ?: "error")
                    }
                }

                override fun onFailure(
                    call: Call<PlaceDetailResponse>,
                    t: Throwable,
                ) {
                    Log.d("오류3", t.message.toString())
                }
            },
        )
    }

    private fun showPlaceDetails(item: PlaceDetailData) {
        binding.tvAmsmBsPlaceName.text = item.placeTitle
        binding.tvAmsmBsAddress.text = item.placeAddress
        binding.tvAmsmBsTime.text = item.timeInfo

        // 이미지 설정
        setPlaceImages(item.placeImgsUrl, R.drawable.default_image)

        // 메뉴 아이템 설정
        placeMenuItems = item.menus
        menuAdapter.updateItems(placeMenuItems)
    }

    private fun setPlaceImages(
        imgUrls: List<String>,
        defaultImgRes: Int,
    ) {
        val imageViews =
            listOf(
                binding.sivAmsmBsImg1,
                binding.sivAmsmBsImg2,
                binding.sivAmsmBsImg3,
            )

        for (i in imageViews.indices) {
            imageViews[i].visibility = View.VISIBLE
            if (i < imgUrls.size) {
                Glide.with(this).load(imgUrls[i]).into(imageViews[i])
            } else {
                Glide.with(this).load(defaultImgRes).into(imageViews[i])
            }
        }
    }

    private fun initMenuRV() {
        placeMenuItems = arrayListOf()

        menuAdapter =
            AddMenuPlaceMenuRVAdapter(
                placeMenuItems,
                onItemSelected = { selectedPosition ->
                    // 아이템이 선택되었을 때 버튼을 활성화, 선택이 취소되면 비활성화
                    binding.btnAmsmNext.isEnabled = selectedPosition != null
                    selectedMenuItem = selectedPosition?.let { placeMenuItems[it] }
                },
                onButtonClicked = {
                    // Bundle을 생성하여 데이터 추가
                    val bundle =
                        Bundle().apply {
                            putString("PLACE_NAME", placeDetailItem.placeTitle)
                            putString("PLACE_ADDRESS", placeDetailItem.placeAddress)
                            putString("PLACE_TIME", placeDetailItem.timeInfo)
                        }

                    // AddMenuNameFragment로 데이터 전달
                    val fragment =
                        AddMenuNameFragment().apply {
                            arguments = bundle
                        }

                    parentFragmentManager
                        .beginTransaction()
                        .addToBackStack("AddMenuSelectMenu")
                        .replace(R.id.cl_add_menu_main, fragment)
                        .commit()
                },
            )

        binding.rvAmsmPlaceMenu.layoutManager = LinearLayoutManager(context)
        binding.rvAmsmPlaceMenu.adapter = menuAdapter

        // 버튼 초기 상태를 비활성화
        binding.btnAmsmNext.isEnabled = false
    }
}
