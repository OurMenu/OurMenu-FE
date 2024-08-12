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
import com.example.ourmenu.data.map.data.MapSearchData
import com.example.ourmenu.data.map.response.MapSearchResponse
import com.example.ourmenu.data.menu.data.MenuPlaceDetailData
import com.example.ourmenu.data.menu.response.MenuPlaceDetailResponse
import com.example.ourmenu.databinding.FragmentMapBinding
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

    private var naverMap: NaverMap? = null
    private var marker: Marker? = null // 마커 관리를 위한 변수

    private var isKeyboardVisible = false

    private var seaarchResultItems: ArrayList<MapSearchData> = ArrayList()

    var selectedPlaceId: Int = 0

    lateinit var menuPlaceItems: ArrayList<MenuPlaceDetailData>

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
                // TODO: 최근 검색에 받아오는 로직 필요

                binding.vMapSearchBg.visibility = View.VISIBLE
                binding.fcvMapMap.visibility = View.GONE
                binding.rvMapSearchResults.visibility = View.VISIBLE
                binding.clMapRecentSearch.visibility = View.VISIBLE

                // TODO: 최근 검색 기록을 어댑터에 설정

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
    private fun fetchMenuPlaceDetail(id: Int) {
        val service = RetrofitObject.retrofit.create(MenuService::class.java)
        val call = service.getMenuPlaceDetail(id)

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
                    TODO("Not yet implemented")
                }
            },
        )
    }

    private fun initSearchResultRV() {
        searchResultAdapter =
            MapSearchResultRVAdapter(arrayListOf()) { data ->
//               binding.etMapSearch.setText(data.placeTitle) // TODO: 클릭된 메뉴 이름으로 세팅
            }

        binding.rvMapSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvMapSearchResults.adapter = searchResultAdapter
    }

    private fun adjustLayoutForKeyboardDismiss() {
        binding.root.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding.root.requestLayout()
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
    }
}
