package com.example.ourmenu.map

import android.content.Context
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
import com.example.ourmenu.data.map.data.MapInfoDetailData
import com.example.ourmenu.data.map.data.MapSearchData
import com.example.ourmenu.data.map.response.MapInfoDetailResponse
import com.example.ourmenu.data.map.response.MapSearchResponse
import com.example.ourmenu.data.menu.data.MenuPlaceDetailData
import com.example.ourmenu.data.menu.response.MenuPlaceDetailResponse
import com.example.ourmenu.databinding.FragmentMapBinding
import com.example.ourmenu.map.adapter.MapBottomSheetRVAdapter
import com.example.ourmenu.map.adapter.MapSearchResultRVAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MapService
import com.example.ourmenu.retrofit.service.MenuService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
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
    private var marker: Marker? = null // 마커 관리를 위한 변수

    private var isKeyboardVisible = false

    private var recentSearchItems: ArrayList<MapSearchData> = ArrayList()
    private var seaarchResultItems: ArrayList<MapSearchData> = ArrayList()

    lateinit var menuPlaceItems: ArrayList<MenuPlaceDetailData>

    lateinit var mapDetailItem: MapInfoDetailData
    var selectedGroupId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.clMapBottomSheet)

        // BottomSheet의 초기 상태를 숨김으로 설정
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        initSearchResultRV()
        initBottomSheetRV()

        // MapFragment 가져오기
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fcv_map_map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    childFragmentManager.beginTransaction().add(R.id.fcv_map_map, it).commit()
                }
        mapFragment.getMapAsync(this)

//        //  키보드 상태 변화 감지해서 화면 길이 조절하기
//        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
//            val rect = Rect()
//            binding.root.getWindowVisibleDisplayFrame(rect)
//            val screenHeight = binding.root.rootView.height
//            val keypadHeight = screenHeight - rect.bottom
//            if (keypadHeight > screenHeight * 0.15) {
//                isKeyboardVisible = true
//            } else {
//                //  키보드 안보일 때
//                if (isKeyboardVisible) {
//                    isKeyboardVisible = false
//                    adjustLayoutForKeyboardDismiss()
//                }
//            }
//        }

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

    private fun showBottomSheetWithMapInfo(data: MapInfoDetailData) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.vMapSearchBg.visibility = View.GONE
        binding.fcvMapMap.visibility = View.VISIBLE
        binding.rvMapSearchResults.visibility = View.GONE
        binding.clMapRecentSearch.visibility = View.GONE

        Log.d("showBottomSheetWithMapInfo", data.toString())

        // Bottom Sheet에 데이터 설정
        bottomSheetAdapter.items = arrayListOf(data) // 클릭한 데이터로 리스트를 설정
        bottomSheetAdapter.notifyDataSetChanged()
    }

    private fun initBottomSheetRV() {
        bottomSheetAdapter =
            MapBottomSheetRVAdapter(arrayListOf()) { data ->
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

    private fun adjustLayoutForKeyboardDismiss() {
        binding.root.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding.root.requestLayout()
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
    }
}
