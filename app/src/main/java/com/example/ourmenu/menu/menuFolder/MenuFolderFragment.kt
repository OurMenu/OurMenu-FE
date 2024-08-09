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
    lateinit var menuFolderItems: ArrayList<MenuFolderData>
    private val retrofit = RetrofitObject.retrofit
    private val menuFolderService = retrofit.create(MenuFolderService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMenuFolderBinding.inflate(inflater, container, false)



        getMenuFolders()

        initItemListener()

        initTouchHelperRV()



        return binding.root
    }

    private fun getMenuFolders() {
        menuFolderService.getMenuFolders().enqueue(
            object : Callback<MenuFolderArrayResponse> {
                override fun onResponse(
                    call: Call<MenuFolderArrayResponse>,
                    response: Response<MenuFolderArrayResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val menuFolders = result?.response
                        menuFolders?.let {
                            menuFolderItems = menuFolders
//                            initTouchHelperRV()

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
            })

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
                override fun onMenuClick(menuFolderId: Int) {
                    val intent = Intent(context, MenuFolderDetailActivity::class.java)
                    intent.putExtra("menuFolderId", menuFolderId)
                    startActivity(intent)
                }

                override fun onEditClick() {
                    // MenuFolderFragment 에서 editClick() 메소드 실행
                    val intent = Intent(context, MenuFolderDetailActivity::class.java)
                    intent.putExtra("isEdit", true)
                    startActivity(intent)
                }

                override fun onDeleteClick() {
                }
            }
    }

    //
    @SuppressLint("ClickableViewAccessibility") // 이줄 없으면 setOnTouchListener 에 밑줄생김
    private fun initTouchHelperRV() {

        val dummyItems = ArrayList<MenuFolderData>()
        for (i in 0..7) {
            dummyItems.add(
                MenuFolderData(
                    menuFolderId = i,
                    menuFolderTitle = "menuFolder$i",
                    menuCount = 7,
                    menuFolderImgUrl = "",
                    menuFolderIcon = "",
                    menuFolderPriority = i + 1,
                    menuIds = arrayListOf()
                )
            )
        }


        val clamp: Float = dpToPx(requireContext(), 120).toFloat()

        val swipeItemTouchHelperCallback =
            SwipeItemTouchHelperCallback().apply {
                setClamp(clamp)
            }

        val menuFolderRVAdapter =
            MenuFolderRVAdapter(
//                menuFolderItems,
                dummyItems,
                requireContext(),
                swipeItemTouchHelperCallback
            ).apply {
                setOnItemClickListener(itemClickListener)
            }

        swipeItemTouchHelperCallback.setAdapter(menuFolderRVAdapter)

        val itemTouchHelper = ItemTouchHelper(swipeItemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvMenuMenuFolder)
        // 리사이클러 뷰 설정
        with(binding.rvMenuMenuFolder) {
            adapter = menuFolderRVAdapter
            // 다른 뷰를 건들면 기존 뷰의 swipe 가 초기화 됨
            setOnTouchListener { _, _ ->
                swipeItemTouchHelperCallback.removePreviousClamp(this)
                false
            }
        }
    }
}
