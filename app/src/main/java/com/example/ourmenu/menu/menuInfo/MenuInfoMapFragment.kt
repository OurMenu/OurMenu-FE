package com.example.ourmenu.menu.menuInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.map.response.MapInfoDetailResponse
import com.example.ourmenu.data.menu.data.MenuImgsUrl
import com.example.ourmenu.data.menu.data.MenuPlaceDetailData
import com.example.ourmenu.data.menu.data.MenuTag
import com.example.ourmenu.databinding.ChipCustomBinding
import com.example.ourmenu.databinding.ChipDefaultBinding
import com.example.ourmenu.databinding.FragmentMenuInfoMapBinding
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MapService
import com.example.ourmenu.util.Utils.getLargeMapPin
import com.example.ourmenu.util.Utils.loadToNaverMap
import com.example.ourmenu.util.Utils.toWon
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Response

class MenuInfoMapFragment :
    Fragment(),
    OnMapReadyCallback {
    lateinit var binding: FragmentMenuInfoMapBinding
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var naverMap: NaverMap? = null
    private var groupId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMenuInfoMapBinding.inflate(inflater, container, false)

        groupId = arguments?.getInt("groupId")

        bottomSheetBehavior = BottomSheetBehavior.from(binding.clMenuInfoMapBottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int,
                ) {
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float,
                ) {
                    adjustButtonPosition()
                }
            },
        )

        // MapFragment 가져오기
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fcv_mim_map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    childFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
                }
        mapFragment.getMapAsync(this)

        binding.ivMenuInfoMapBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun fetchPlaceInfo(groupId: Int) {
        val service = RetrofitObject.retrofit.create(MapService::class.java)
        val call = service.getMapInfoDetail(groupId)

        call.enqueue(
            object : retrofit2.Callback<MapInfoDetailResponse> {
                override fun onResponse(
                    call: Call<MapInfoDetailResponse>,
                    response: Response<MapInfoDetailResponse>,
                ) {
                    if (response.isSuccessful) {
                        val mapInfoDetail = response.body()?.response
                        mapInfoDetail?.let {
                            showPlaceInfo(it)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<MapInfoDetailResponse>,
                    t: Throwable,
                ) {
                    Log.d("fetchMapInfoDetail", t.message.toString())
                }
            },
        )
    }

    private fun showPlaceInfo(data: MenuPlaceDetailData) {
        val mapx = data.longitude
        val mapy = data.latitude

        // 지도에 핀 찍기 및 지도의 focus를 해당 위치로 이동
        naverMap?.let { naverMap ->
            val marker =
                Marker().apply {
                    position = LatLng(mapy, mapx)
                    icon = OverlayImage.fromResource(getLargeMapPin(data.menuIconType)) // 핀 아이콘 설정
                    map = naverMap
                }

            naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(mapy, mapx)))
        }

        binding.tvMenuInfoMapBsMenu.text = data.menuTitle
        binding.tvMenuInfoMapBsPrice.text = toWon(data.menuPrice)
        binding.tvMenuInfoMapBsPlace.text = data.placeTitle

//        binding.ivMenuInfoMapFolderChipIcon.setImageResource() //TODO: 아이콘 이미지 반영
        val folderText =
            if (data.menuFolder.menuFolderCount == 0) {
                data.menuFolder.menuFolderTitle
            } else {
                "${data.menuFolder.menuFolderTitle} +${data.menuFolder.menuFolderCount}"
            }
        binding.tvMenuInfoMapFolderChipText.text = folderText

        // TODO: 기본 이미지 변경
        setMenuImages(data.menuImgsUrl, R.drawable.menu_sample)
        setChips(data.menuTags)

        binding.clMenuInfoMapGotoMapBtn.setOnClickListener {
            loadToNaverMap(requireContext(), mapy, mapx, data.placeTitle)
        }
    }

    private fun setMenuImages(
        imgUrls: ArrayList<MenuImgsUrl>,
        defaultImgRes: Int,
    ) {
        val imageViews =
            arrayListOf(
                binding.sivMenuInfoMapBsImg1,
                binding.sivMenuInfoMapBsImg2,
                binding.sivMenuInfoMapBsImg3,
            )

        for (i in imageViews.indices) {
            if (i < imgUrls.size) {
                Glide
                    .with(binding.root.context)
                    .load(imgUrls[i].menuImgUrl)
                    .into(imageViews[i])
            } else {
                Glide
                    .with(binding.root.context)
                    .load(defaultImgRes)
                    .into(imageViews[i])
            }
        }
    }

    private fun setChips(tags: ArrayList<MenuTag>) {
        // ChipGroup의 기존 Chip들 제거
        binding.cgMimChipGroup.removeAllViews()

        // ChipGroup에 Chip 추가
        for (tag in tags) {
            val inflater = LayoutInflater.from(binding.root.context)
            val customTagBinding = ChipCustomBinding.inflate(inflater, binding.cgMimChipGroup, false)
            val defaultTagBinding = ChipDefaultBinding.inflate(inflater, binding.cgMimChipGroup, false)

            if (tag.custom) {
                customTagBinding.tvTagDefaultTag.text = tag.tagTitle
                binding.cgMimChipGroup.addView(customTagBinding.root)
            } else {
                defaultTagBinding.tvTagDefaultTag.text = tag.tagTitle
                binding.cgMimChipGroup.addView(defaultTagBinding.root)
            }
        }
    }

    private fun adjustButtonPosition() {
        val buttonHeight = binding.clMenuInfoMapGotoMapBtn.height
        val bottomSheetHeight = binding.clMenuInfoMapBottomSheet.height
        val bottomSheetTop = binding.clMenuInfoMapBottomSheet.top
        val parentHeight = binding.root.height

        val newButtonY = bottomSheetTop - buttonHeight - 42

        binding.clMenuInfoMapGotoMapBtn.y = newButtonY.toFloat()
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 전달받은 groupId가 있는 경우에만 fetchPlaceInfo 호출
        groupId?.let {
            fetchPlaceInfo(it)
        }
//        // TODO: 진짜 groupId 받아오기
//        fetchPlaceInfo(2)
    }
}
