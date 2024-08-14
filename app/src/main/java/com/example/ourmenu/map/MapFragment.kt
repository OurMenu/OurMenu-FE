package com.example.ourmenu.map

import android.content.Context
import android.content.Intent
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
import com.example.ourmenu.addMenu.AddMenuActivity
import com.example.ourmenu.data.map.data.MapData
import com.example.ourmenu.data.map.data.MapSearchData
import com.example.ourmenu.data.map.response.MapInfoDetailResponse
import com.example.ourmenu.data.map.response.MapResponse
import com.example.ourmenu.data.map.response.MapSearchResponse
import com.example.ourmenu.data.menu.data.MenuPlaceDetailData
import com.example.ourmenu.data.menu.response.MenuPlaceDetailResponse
import com.example.ourmenu.databinding.FragmentMapBinding
import com.example.ourmenu.map.adapter.MapBottomSheetRVAdapter
import com.example.ourmenu.map.adapter.MapSearchResultRVAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MapService
import com.example.ourmenu.retrofit.service.MenuService
import com.example.ourmenu.util.Utils.dpToPx
import com.example.ourmenu.util.Utils.getLargeMapPin
import com.example.ourmenu.util.Utils.getSmallMapPin
import com.example.ourmenu.util.Utils.loadToNaverMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class MapFragment :
    Fragment(),
    OnMapReadyCallback {
    lateinit var binding: FragmentMapBinding
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var searchResultAdapter: MapSearchResultRVAdapter
    lateinit var bottomSheetAdapter: MapBottomSheetRVAdapter

    private var naverMap: NaverMap? = null

    // 현재 선택된 마커를 추적하기 위한 변수
    private var selectedMarker: Marker? = null

    private var recentSearchItems: ArrayList<MapSearchData> = ArrayList()
    private var seaarchResultItems: ArrayList<MapSearchData> = ArrayList()

    lateinit var menuPlaceItems: ArrayList<MenuPlaceDetailData>

    lateinit var mapItem: ArrayList<MapData>

    private val markers = ArrayList<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.clMapBottomSheet)

        // BottomSheet의 초기 상태를 숨김으로 설정
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.clMapBottomSheet.viewTreeObserver.addOnGlobalLayoutListener {
            context?.let { ctx ->
                val maxHeight = dpToPx(ctx, 540)

                if (binding.clMapBottomSheet.height > maxHeight) {
                    val layoutParams = binding.clMapBottomSheet.layoutParams
                    layoutParams.height = maxHeight
                    binding.clMapBottomSheet.layoutParams = layoutParams
                }
            }
        }

        // BottomSheetCallback을 사용하여 BottomSheet의 움직임을 감지
        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int,
                ) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            binding.clMapMapGotoMapBtn.visibility = View.VISIBLE

                            binding.clMapBottomSheet.visibility = View.VISIBLE
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            // 선택된 마커의 아이콘을 원래 상태로 되돌림
                            selectedMarker?.let { marker ->
                                // marker에 연결된 데이터에서 menuIconType 가져오기
                                val menuIconType = marker.tag as? String
                                // menuIconType에 따라 작은 아이콘으로 설정
                                menuIconType?.let {
                                    marker.icon = OverlayImage.fromResource(getSmallMapPin(it))
                                }
                            }
                            selectedMarker = null

                            // 검색 필드를 지움
                            binding.etMapSearch.text.clear()

                            // BottomSheet를 완전히 숨기기 위해 visibility를 GONE으로 설정
                            binding.clMapBottomSheet.visibility = View.GONE
                        }

                        else -> {
                            binding.clMapMapGotoMapBtn.visibility = View.GONE

                            binding.clMapBottomSheet.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float,
                ) {
//                    adjustButtonPosition()
                }
            },
        )

        initSearchResultRV()
        initBottomSheetRV()

        // MapFragment 가져오기
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fcv_map_map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    childFragmentManager.beginTransaction().add(R.id.fcv_map_map, it).commit()
                }
        mapFragment.getMapAsync(this)

        binding.ivMapAddBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddMenuActivity::class.java)
            startActivity(intent)
        }

        // 검색바 focus됐을 때
        binding.etMapSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                fetchSearchHistory()

                binding.vMapSearchBg.visibility = View.VISIBLE
                binding.fcvMapMap.visibility = View.GONE
                binding.rvMapSearchResults.visibility = View.VISIBLE
                binding.clMapRecentSearch.visibility = View.VISIBLE

                binding.etMapSearch.text.clear() // 검색바 클릭하면 필드 비우기

                // bottom sheet가 떠있는 상태에서 검색바를 클릭하면 bottom sheet가 사라지도록
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                binding.vMapSearchBg.visibility = View.GONE
                binding.fcvMapMap.visibility = View.VISIBLE
                binding.rvMapSearchResults.visibility = View.GONE
                binding.clMapRecentSearch.visibility = View.GONE
            }
        }

        // 검색바에서 엔터 키 이벤트 처리
        binding.etMapSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                val title = binding.etMapSearch.text.toString()
                binding.clMapRecentSearch.visibility = View.GONE

                // 검색 하면 키보드 숨기기
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etMapSearch.windowToken, 0)

                fetchSearchResult(title)
                true
            } else {
                false
            }
        }

        return binding.root
    }

    // 지도에 pin 찍을 모든 정보 받아오기
    private fun fetchMapInfo() {
        val service = RetrofitObject.retrofit.create(MapService::class.java)
        val call = service.getMapInfo()

        call.enqueue(
            object : retrofit2.Callback<MapResponse> {
                override fun onResponse(
                    call: Call<MapResponse>,
                    response: Response<MapResponse>,
                ) {
                    if (response.isSuccessful) {
                        val mapInfoResponse = response.body()

                        if (mapInfoResponse?.isSuccess == true) {
                            mapItem = mapInfoResponse.response

                            showMapInfo(mapItem)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<MapResponse>,
                    t: Throwable,
                ) {
                    TODO("Not yet implemented")
                }
            },
        )
    }

    // 최신 검색 정보 받아오기
    private fun fetchSearchHistory() {
        val service = RetrofitObject.retrofit.create(MapService::class.java)
        val call = service.getMapSearchHistory()

        call.enqueue(
            object : retrofit2.Callback<MapSearchResponse> {
                override fun onResponse(
                    call: Call<MapSearchResponse>,
                    response: Response<MapSearchResponse>,
                ) {
                    if (response.isSuccessful) {
                        val searchHistoryResponse = response.body()

                        if (searchHistoryResponse?.isSuccess == true) {
                            recentSearchItems = searchHistoryResponse.response
                            searchResultAdapter.updateItemsFromSearch(recentSearchItems)
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
                    call: Call<MapSearchResponse>,
                    t: Throwable,
                ) {
                    Log.d("오류3", t.message.toString())
                }
            },
        )
    }

    // 검색 결과
    private fun fetchSearchResult(title: String) {
        val service = RetrofitObject.retrofit.create(MapService::class.java)
        val call = service.getMapSearch(title)

        call.enqueue(
            object : retrofit2.Callback<MapSearchResponse> {
                override fun onResponse(
                    call: Call<MapSearchResponse>,
                    response: Response<MapSearchResponse>,
                ) {
                    if (response.isSuccessful) {
                        val searchResultResponse = response.body()

                        if (searchResultResponse?.isSuccess == true) {
                            val searchResult = searchResultResponse.response

                            if (searchResult != null && searchResult.isNotEmpty()) {
                                seaarchResultItems = searchResult
                                searchResultAdapter.updateItemsFromSearch(seaarchResultItems)

                                binding.rvMapSearchResults.visibility = View.VISIBLE
                                binding.clMapNoResult.visibility = View.GONE
                            } else {
                                binding.rvMapSearchResults.visibility = View.GONE
                                binding.clMapNoResult.visibility = View.VISIBLE
                                binding.tvMapNoResult.text = "검색된 결과가 없습니다."
                            }
                        } else {
                            val errorMessage = searchResultResponse?.errorResponse?.message ?: "에러"

                            binding.rvMapSearchResults.visibility = View.GONE
                            binding.clMapNoResult.visibility = View.VISIBLE
                            binding.tvMapNoResult.text = errorMessage
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()

                        // Error response를 처리하여 message 추출
                        val errorMessage =
                            errorResponse?.let {
                                try {
                                    val jsonObject = JSONObject(it)
                                    jsonObject.getJSONObject("errorResponse").getString("message")
                                } catch (e: JSONException) {
                                    "알 수 없는 에러가 발생했습니다."
                                }
                            } ?: "알 수 없는 에러가 발생했습니다."

                        binding.rvMapSearchResults.visibility = View.GONE
                        binding.clMapNoResult.visibility = View.VISIBLE
                        binding.tvMapNoResult.text = errorMessage
                    }
                }

                override fun onFailure(
                    call: Call<MapSearchResponse>,
                    t: Throwable,
                ) {
                    Log.d("MapSearchResponse", t.message.toString())
                }
            },
        )
    }

    // 지도에서 핀 클릭했을 때
    private fun fetchMenuPlaceDetail(placeID: Int) {
        val service = RetrofitObject.retrofit.create(MenuService::class.java)
        val call = service.getMenuPlaceDetail(placeID)

        call.enqueue(
            object : retrofit2.Callback<MenuPlaceDetailResponse> {
                override fun onResponse(
                    call: Call<MenuPlaceDetailResponse>,
                    response: Response<MenuPlaceDetailResponse>,
                ) {
                    if (response.isSuccessful) {
                        val menuPlaceResponse = response.body()

                        if (menuPlaceResponse?.isSuccess == true) {
                            val menuPlaceInfo = menuPlaceResponse.response

                            if (menuPlaceInfo.isNotEmpty()) {
                                menuPlaceItems = menuPlaceInfo

                                // Bottom Sheet에 정보 표시
                                showBottomSheetWithMenuPlaceInfo(menuPlaceInfo)
                            }
                        }
                    }
                }

                override fun onFailure(
                    call: Call<MenuPlaceDetailResponse>,
                    t: Throwable,
                ) {
                    Log.d("fetchMenuPlaceDetail", t.message.toString())
                }
            },
        )
    }

    // 검색 결과 클릭했을 때
    private fun fetchMapInfoDetail(groupId: Int) {
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
                            showBottomSheetWithMapInfo(it)
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

    // 검색 결과 클릭 시
    private fun onSearchResultClick(groupId: Int) {
        // 키보드를 숨기기
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etMapSearch.windowToken, 0)

        // 검색 결과 클릭 후 포커스를 제거하여 재클릭 시 포커스 이벤트가 트리거되도록 함
        binding.etMapSearch.clearFocus()

        fetchMapInfoDetail(groupId)
    }

    // 핀 찍을 때
    private fun showMapInfo(data: ArrayList<MapData>) {
        // 기존 마커 제거
        markers.forEach { it.map = null }
        markers.clear()

        for (item in data) {
            val marker =
                Marker().apply {
                    position = LatLng(item.latitude, item.longitude)
                    icon = OverlayImage.fromResource(getSmallMapPin(item.menuIconType)) // menuIconType에 따른 아이콘 설정
                    map = naverMap
                    tag = item.menuIconType // 마커에 menuIconType을 태그로 저장

                    // 마커 클릭 이벤트 설정
                    setOnClickListener {
                        // 이전에 선택된 마커가 있으면 원래 상태로 되돌리기
                        selectedMarker?.let { previousMarker ->
                            val previousMenuIconType = previousMarker.tag as? String
                            previousMenuIconType?.let {
                                previousMarker.icon = OverlayImage.fromResource(getSmallMapPin(it))
                            }
                        }

                        // 현재 마커를 선택된 마커로 설정하고 아이콘 변경
                        selectedMarker = this
                        val menuIconType = item.menuIconType
                        icon = OverlayImage.fromResource(getLargeMapPin(menuIconType))

                        // 클릭한 마커의 placeId로 fetchMenuPlaceDetail 호출
                        fetchMenuPlaceDetail(item.placeId)

                        // 핀 클릭하면 검색창 비우기
                        binding.etMapSearch.text.clear()

                        true // 클릭 이벤트 소비
                    }
                }
            markers.add(marker)
        }
    }

    private fun showBottomSheetWithMapInfo(data: MenuPlaceDetailData) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.vMapSearchBg.visibility = View.GONE
        binding.fcvMapMap.visibility = View.VISIBLE
        binding.rvMapSearchResults.visibility = View.GONE
        binding.clMapRecentSearch.visibility = View.GONE

        // Bottom Sheet에 데이터 설정
        bottomSheetAdapter.items = arrayListOf(data) // 클릭한 데이터로 리스트를 설정
        bottomSheetAdapter.notifyDataSetChanged()

        val mapx = data.longitude
        val mapy = data.latitude

        // 기존 선택된 마커 초기화 (이전 마커 아이콘 원래대로)
        selectedMarker?.let { marker ->
            val menuIconType = marker.tag as? String
            menuIconType?.let {
                marker.icon = OverlayImage.fromResource(getSmallMapPin(it))
            }
        }
        selectedMarker = null

        // 마커 리스트에서 해당 위치의 마커를 찾아 아이콘을 변경
        selectedMarker = markers.find { it.position.latitude == mapy && it.position.longitude == mapx }
        selectedMarker?.let { marker ->
            val menuIconType = marker.tag as? String
            menuIconType?.let {
                marker.icon = OverlayImage.fromResource(getLargeMapPin(it))
            }
        }

        // 지도의 focus를 해당 위치로 이동
        naverMap?.moveCamera(CameraUpdate.scrollTo(LatLng(mapy, mapx)))

        binding.clMapMapGotoMapBtn.setOnClickListener {
            loadToNaverMap(requireContext(), mapy, mapx, data.placeTitle)
        }
    }

    private fun showBottomSheetWithMenuPlaceInfo(menuPlaceInfo: ArrayList<MenuPlaceDetailData>) {
        if (menuPlaceInfo.isNotEmpty()) {
            bottomSheetAdapter.items = menuPlaceInfo
            bottomSheetAdapter.notifyDataSetChanged()

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            binding.vMapSearchBg.visibility = View.GONE
            binding.fcvMapMap.visibility = View.VISIBLE
            binding.rvMapSearchResults.visibility = View.GONE
            binding.clMapRecentSearch.visibility = View.GONE

            val mapx = menuPlaceInfo[0].longitude
            val mapy = menuPlaceInfo[0].latitude

            binding.clMapMapGotoMapBtn.setOnClickListener {
                loadToNaverMap(requireContext(), mapy, mapx, menuPlaceInfo[0].placeTitle)
            }
        } else {
            Log.d("showBottomSheet", "Menu place info is empty.")
        }
    }

    private fun initBottomSheetRV() {
        bottomSheetAdapter =
            MapBottomSheetRVAdapter(arrayListOf()) { data ->
                // TODO: 클릭하면 메뉴 상세 화면으로 이동시키기
            }

        binding.rvMapBottomSheet.layoutManager = LinearLayoutManager(context)
        binding.rvMapBottomSheet.adapter = bottomSheetAdapter
    }

    private fun initSearchResultRV() {
        searchResultAdapter =
            MapSearchResultRVAdapter(arrayListOf()) { data ->
                binding.etMapSearch.setText(data.menuTitle)

                onSearchResultClick(data.groupId)
            }

        binding.rvMapSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvMapSearchResults.adapter = searchResultAdapter

        fetchSearchHistory()
    }

    private fun adjustButtonPosition() {
        val buttonTop = binding.clMapMapGotoMapBtn.top
        val bottomSheetTop = binding.clMapBottomSheet.top

        val newButtonY = bottomSheetTop - buttonTop

        binding.clMapMapGotoMapBtn.y = newButtonY.toFloat()
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 초기 위치 설정 (건국대학교 서울캠퍼스)
        val initialPosition = LatLng(37.5408, 127.0789)

        // 지도의 focus를 초기 위치로 이동
        naverMap?.moveCamera(CameraUpdate.scrollTo(initialPosition))

        // 지도에 핀을 찍기 위해 지도 정보 불러오기
        fetchMapInfo()
    }
}
