package com.example.ourmenu.addMenu

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourmenu.R
import com.example.ourmenu.addMenu.adapter.AddMenuSearchResultRVAdapter
import com.example.ourmenu.data.place.PlaceInfoData2
import com.example.ourmenu.data.place.PlaceInfoResponse
import com.example.ourmenu.data.place.PlaceSearchHistoryData
import com.example.ourmenu.data.place.PlaceSearchHistoryResponse
import com.example.ourmenu.databinding.FragmentAddMenuMapBinding
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.PlaceService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call

class AddMenuMapFragment :
    Fragment(),
    OnMapReadyCallback {
    lateinit var binding: FragmentAddMenuMapBinding
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var resultAdapter: AddMenuSearchResultRVAdapter

    private var recentSearchItems: ArrayList<PlaceSearchHistoryData> = ArrayList()
    private var searchResultItems: ArrayList<PlaceInfoData2> = ArrayList()

    private var isKeyboardVisible = false

    private var naverMap: NaverMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddMenuMapBinding.inflate(inflater, container, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.clAddMenuBottomSheet)

        // BottomSheet의 초기 상태를 숨김으로 설정
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        initResultRV()

        // MapFragment 가져오기
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fcv_add_menu_map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    childFragmentManager.beginTransaction().add(R.id.fcv_add_menu_map, it).commit()
                }
        mapFragment.getMapAsync(this)

        //  키보드 상태 변화 감지해서 화면 길이 조절하기
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                isKeyboardVisible = true
            } else {
                //  키보드 안보일 때
                if (isKeyboardVisible) {
                    isKeyboardVisible = false
                    adjustLayoutForKeyboardDismiss()
                }
            }
        }

        // BottomSheet 상태 변화 리스너 설정
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
                }
            },
        )

        // 뒤로가기 버튼 클릭 이벤트 처리
        binding.ivAddMenuLogoBack.setOnClickListener {
            binding.etAddMenuSearch.text.clear() // 입력 필드 비우기 추가
            handleBackPress()
        }

        // 검색바 focus됐을 때
        binding.etAddMenuSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.vAddMenuSearchBg.visibility = View.VISIBLE
                binding.fcvAddMenuMap.visibility = View.GONE
                binding.rvAddMenuSearchResults.visibility = View.VISIBLE
                binding.clAddMenuRecentSearch.visibility = View.VISIBLE
                binding.btnAddMenuNoResult.visibility = View.VISIBLE

                // 최근 검색 기록을 어댑터에 설정
                resultAdapter.updateItemsFromSearchHistory(recentSearchItems)

                binding.etAddMenuSearch.text.clear() // 입력 필드 비우기

                // bottom sheet가 떠있는 상태에서 검색바를 클릭하면 bottom sheet가 사라지도록
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                binding.vAddMenuSearchBg.visibility = View.GONE
                binding.fcvAddMenuMap.visibility = View.VISIBLE
                binding.rvAddMenuSearchResults.visibility = View.GONE
                binding.clAddMenuRecentSearch.visibility = View.GONE
                binding.btnAddMenuNoResult.visibility = View.GONE
            }
        }

        // 검색바에서 엔터 키 이벤트 처리
        binding.etAddMenuSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                performSearch(binding.etAddMenuSearch.text.toString())
                true
            } else {
                false
            }
        }

        // 검색 결과 없을 때 버튼 이벤트 처리
        binding.btnAddMenuNoResult.setOnClickListener {
            binding.etAddMenuSearch.text.clear() // 입력 필드 비우기 추가

            // 키보드 숨기기
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etAddMenuSearch.windowToken, 0)

            parentFragmentManager
                .beginTransaction()
                .addToBackStack("AddMenuMap")
                .replace(R.id.cl_add_menu_main, AddMenuNameFragment())
                .commit()
        }

        binding.btnAddMenuNext.setOnClickListener {
            binding.etAddMenuSearch.text.clear() // 입력 필드 비우기 추가

            parentFragmentManager
                .beginTransaction()
                .addToBackStack("AddMenuMap")
                .replace(R.id.cl_add_menu_main, AddMenuSelectMenuFragment())
                .commit()
        }

        return binding.root
    }

    private fun fetchPlaceInfo(title: String) {
        val service = RetrofitObject.retrofit.create(PlaceService::class.java)
        val call = service.getPlaceInfo("Bearer " + RetrofitObject.TOKEN, title)

        call.enqueue(
            object : retrofit2.Callback<PlaceInfoResponse> {
                override fun onResponse(
                    call: Call<PlaceInfoResponse>,
                    response: retrofit2.Response<PlaceInfoResponse>,
                ) {
                    if (response.isSuccessful) {
                        val placeInfoResponse = response.body()

                        if (placeInfoResponse?.isSuccess == true) {
                            val placeInfo = placeInfoResponse.response
                            if (placeInfo != null) {
                                searchResultItems = placeInfo
                                resultAdapter.updateItemsFromSearchResults(searchResultItems)
                            }
                        } else {
                            val errorMessage = placeInfoResponse?.errorResponse?.message ?: "error"
                            Log.d("오류1", errorMessage)
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        Log.d("오류2", errorResponse ?: "error")
                    }
                }

                override fun onFailure(
                    call: Call<PlaceInfoResponse>,
                    t: Throwable,
                ) {
                    Log.d("오류3", t.message.toString())
                }
            },
        )
    }

    private fun fetchSearchHistoryInfo() {
        val service = RetrofitObject.retrofit.create(PlaceService::class.java)
        val call = service.getPlaceSearchHistory("Bearer " + RetrofitObject.TOKEN)

        call.enqueue(
            object : retrofit2.Callback<PlaceSearchHistoryResponse> {
                override fun onResponse(
                    call: Call<PlaceSearchHistoryResponse>,
                    response: retrofit2.Response<PlaceSearchHistoryResponse>,
                ) {
                    if (response.isSuccessful) {
                        val searchHistoryResponse = response.body()
                        if (searchHistoryResponse?.isSuccess == true) {
                            recentSearchItems = searchHistoryResponse.response
                            resultAdapter.updateItemsFromSearchHistory(recentSearchItems)
                        } else {
                            val errorMessage = searchHistoryResponse?.errorResponse?.message ?: "error"
                            Log.d("오류1", errorMessage)
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        Log.d("오류2", errorResponse ?: "error")
                    }
                }

                override fun onFailure(
                    call: Call<PlaceSearchHistoryResponse>,
                    t: Throwable,
                ) {
                    Log.d("오류3", t.message.toString())
                }
            },
        )
    }

    private fun initResultRV() {
        resultAdapter =
            AddMenuSearchResultRVAdapter(arrayListOf()) { place ->
                if (place is PlaceSearchHistoryData) {
                    binding.etAddMenuSearch.setText(place.storeName)
                } else if (place is PlaceInfoData2) {
                    showPlaceDetails(place) // 장소 세부 정보를 표시
                    binding.etAddMenuSearch.setText(place.name) // 검색 결과 item의 placeName으로 input field 설정
                }
                returnToMap()
            }
        binding.rvAddMenuSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvAddMenuSearchResults.adapter = resultAdapter

        fetchSearchHistoryInfo() // 어댑터 초기화 시 검색 기록으로 설정
    }

    private fun returnToMap() {
        binding.rvAddMenuSearchResults.visibility = View.GONE
        binding.etAddMenuSearch.clearFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etAddMenuSearch.windowToken, 0)
    }

    private fun adjustLayoutForKeyboardDismiss() {
        binding.root.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding.root.requestLayout()
    }

    private fun showPlaceDetails(item: PlaceInfoData2) {
        binding.tvAddMenuBsPlaceName.text = item.name
        binding.tvAddMenuBsAddress.text = item.address
        binding.tvAddMenuBsTime.text = item.time
//        binding.sivAddMenuBsImg1.setImageResource(item.imgs[0])
//        binding.sivAddMenuBsImg2.setImageResource(item.imgs[1])
//        binding.sivAddMenuBsImg3.setImageResource(item.imgs[2])

        // 이미지 로드
//        if (item.images.isNotEmpty()) {
//            binding.sivAddMenuBsImg1.visibility = View.VISIBLE
//            binding.sivAddMenuBsImg2.visibility = View.VISIBLE
//            binding.sivAddMenuBsImg3.visibility = View.VISIBLE

        // Glide를 사용해 이미지를 로드하는 예시 (Glide는 의존성 추가 필요)
//            Glide.with(this).load(item.images[0]).into(binding.sivAddMenuBsImg1)
//            Glide.with(this).load(item.images[1]).into(binding.sivAddMenuBsImg2)
//            Glide.with(this).load(item.images[2]).into(binding.sivAddMenuBsImg3)
//        } else {
        binding.sivAddMenuBsImg1.visibility = View.GONE
        binding.sivAddMenuBsImg2.visibility = View.GONE
        binding.sivAddMenuBsImg3.visibility = View.GONE
//        }

//        // 키보드 숨기기
//        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(binding.etAddMenuSearch.windowToken, 0)

        // 지도에 핀 찍기
        val mapx = item.mapx.toDouble()
        val mapy = item.mapy.toDouble()
        val marker = Marker()
        marker.position = LatLng(mapy, mapx)
        marker.map = naverMap

        // 자체 아이콘으로 pin 설정
        marker.icon = OverlayImage.fromResource(R.drawable.ic_map_pin_add)

        // 지도의 focus를 해당 위치로 이동
        naverMap?.moveCamera(CameraUpdate.scrollTo(LatLng(mapy, mapx)))

        // 키보드가 사라질 때 Bottom Sheet가 화면의 가장 아래에 위치하도록
        binding.clAddMenuBottomSheet.postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }, 100)
    }

    private fun handleBackPress() {
        if (binding.vAddMenuSearchBg.visibility == View.VISIBLE ||
            binding.rvAddMenuSearchResults.visibility == View.VISIBLE
        ) {
            // 검색 화면이 보일 때 -> 지도 화면으로 전환
            binding.vAddMenuSearchBg.visibility = View.GONE
            binding.fcvAddMenuMap.visibility = View.VISIBLE
            binding.rvAddMenuSearchResults.visibility = View.GONE
            binding.clAddMenuRecentSearch.visibility = View.GONE
            binding.etAddMenuSearch.clearFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etAddMenuSearch.windowToken, 0)
        } else {
            // 지도 화면이 보일 때 -> 이전 화면으로 돌아가기
            requireActivity().onBackPressed()
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            binding.rvAddMenuSearchResults.visibility = View.GONE
            binding.clAddMenuNoResult.visibility = View.VISIBLE
        } else {
            binding.clAddMenuRecentSearch.visibility = View.GONE
            fetchPlaceInfo(query)
        }

        // 검색 하면 키보드 숨기기
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etAddMenuSearch.windowToken, 0)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
    }
}
