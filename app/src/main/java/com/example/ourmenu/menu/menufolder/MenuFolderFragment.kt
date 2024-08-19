package com.example.ourmenu.menu.menuFolder

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.ourmenu.addMenu.AddMenuActivity
import com.example.ourmenu.data.BaseResponse
import com.example.ourmenu.data.menuFolder.data.MenuFolderData
import com.example.ourmenu.data.menuFolder.response.MenuFolderArrayResponse
import com.example.ourmenu.databinding.FragmentMenuFolderBinding
import com.example.ourmenu.menu.adapter.MenuFolderRVAdapter
import com.example.ourmenu.menu.callback.SwipeItemTouchHelperCallback
import com.example.ourmenu.menu.iteminterface.MenuFolderItemClickListener
import com.example.ourmenu.menu.menuFolder.post.PostMenuFolderActivity
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuFolderService
import com.example.ourmenu.util.Utils.dpToPx
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFolderFragment : Fragment() {
    lateinit var binding: FragmentMenuFolderBinding
    lateinit var itemClickListener: MenuFolderItemClickListener
    private val menuFolderItems = ArrayList<MenuFolderData>()
    lateinit var rvAdapter: MenuFolderRVAdapter
    lateinit var swipeItemTouchHelperCallback: SwipeItemTouchHelperCallback
    private var allMenuCount = 0

    private val retrofit = RetrofitObject.retrofit
    private val menuFolderService = retrofit.create(MenuFolderService::class.java)

    override fun onStart() {
        super.onStart()
        if (menuFolderItems.size > 0) {
            getMenuFolders()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMenuFolderBinding.inflate(inflater, container, false)

        getMenuFolders()
        initTouchHelper()
        initItemListener()

        return binding.root
    }

    private fun getMenuFolders() {
        menuFolderService.getMenuFolders().enqueue(
            object : Callback<MenuFolderArrayResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<MenuFolderArrayResponse>,
                    response: Response<MenuFolderArrayResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val menuFolders = result?.response
                        menuFolders?.let {
                            if (menuFolderItems.size == 0) {
                                menuFolderItems.addAll(menuFolders.menuFolders)
                                for (menuFolder in menuFolderItems) {
                                    allMenuCount = menuFolders.menuCount
                                }
                                binding.tvMenuFolderAllMenuCount.text = "메뉴 ${allMenuCount}개"
                            }
                            initRV()
                            rvAdapter.updateList(menuFolders.menuFolders)
                        }
                    } else {
                        Log.d("err", response.errorBody().toString())
                    }
                }

                override fun onFailure(
                    call: Call<MenuFolderArrayResponse>,
                    t: Throwable,
                ) {
                    Log.d("menuFolders", t.message.toString())
                }
            },
        )
    }

    private fun initItemListener() {
        // 상단 메뉴 추가
        binding.ivMenuAddMenu.setOnClickListener {
            // TODO API 추가
            val intent = Intent(context, AddMenuActivity::class.java)
            startActivity(intent)
        }

        // 메뉴판 추가하기
        binding.btnMenuAddMenuFolder.setOnClickListener {
            val intent = Intent(context, PostMenuFolderActivity::class.java)
            startActivity(intent)
        }

        // 전체 메뉴판 보기
        binding.ivMenuAllMenu.setOnClickListener {
            val intent = Intent(context, MenuFolderDetailActivity::class.java)
            intent.putExtra("isAll", true)
            startActivity(intent)
        }

        itemClickListener =
            object : MenuFolderItemClickListener {
                override fun onMenuClick(menuFolderId: Int, menuFolderTitle: String?, menuFolderImgUrl: String?) {
                    val intent = Intent(context, MenuFolderDetailActivity::class.java)
                    intent.putExtra("menuFolderId", menuFolderId)
                    intent.putExtra("menuFolderTitle", menuFolderTitle)
                    intent.putExtra("menuFolderImgUrl", menuFolderImgUrl)
                    startActivity(intent)
                }

                override fun onEditClick(menuFolderId: Int) {
                    // MenuFolderFragment 에서 editClick() 메소드 실행
                    val intent = Intent(context, MenuFolderDetailActivity::class.java)
                    intent.putExtra("menuFolderId", menuFolderId)
                    intent.putExtra("isEdit", true)
                    startActivity(intent)
                }

                override fun onDeleteClick(
                    menuFolderId: Int,
                    position: Int,
                ) {
                    // TODO 삭제버튼 클릭 API 구현
                    showDeleteDialog()

                    // /menuFolder/{menuFolderId} DELETE API
                    menuFolderService.deleteMenuFolder(menuFolderId).enqueue(
                        object : Callback<BaseResponse> {
                            override fun onResponse(
                                call: Call<BaseResponse>,
                                response: Response<BaseResponse>,
                            ) {
                                if (response.isSuccessful) {
                                    val result = response.body()
                                    Log.d("deleteMenuFolder", result.toString())
                                    menuFolderItems.removeAt(position)
                                    rvAdapter.notifyItemRemoved(position)
                                    rvAdapter.notifyItemRangeRemoved(position, menuFolderItems.size - position)
                                }
                            }

                            override fun onFailure(
                                call: Call<BaseResponse>,
                                t: Throwable,
                            ) {
                                Log.d("deleteMenuFolder", t.toString())
                            }
                        },
                    )
                }
            }
    }

    private fun showDeleteDialog() {
        TODO("Not yet implemented")
    }

    @SuppressLint("ClickableViewAccessibility") // 이줄 없으면 setOnTouchListener 에 밑줄생김
    private fun initRV() {
        rvAdapter =
            MenuFolderRVAdapter(
                menuFolderItems,
                requireContext(),
                swipeItemTouchHelperCallback,
            ).apply {
                setOnItemClickListener(itemClickListener)
            }

        Log.d("mi", menuFolderItems.size.toString())

        swipeItemTouchHelperCallback.setAdapter(rvAdapter)

        val itemTouchHelper = ItemTouchHelper(swipeItemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvMenuMenuFolder)
        // 리사이클러 뷰 설정
        with(binding.rvMenuMenuFolder) {
            adapter = rvAdapter
            // 다른 뷰를 건들면 기존 뷰의 swipe 가 초기화 됨
            setOnTouchListener { _, _ ->
                swipeItemTouchHelperCallback.removePreviousClamp(this)
                false
            }
        }
    }

    //
    private fun initTouchHelper() {
        val clamp: Float = dpToPx(requireContext(), 120).toFloat()

        swipeItemTouchHelperCallback =
            SwipeItemTouchHelperCallback().apply {
                setClamp(clamp)
            }
    }
}
