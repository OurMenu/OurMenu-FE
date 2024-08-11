package com.example.ourmenu.menu.menuFolder

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import com.example.ourmenu.R
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.data.menu.response.MenuArrayResponse
import com.example.ourmenu.databinding.FragmentMenuFolderDetailAllBinding
import com.example.ourmenu.menu.adapter.MenuFolderAllFilterSpinnerAdapter
import com.example.ourmenu.menu.adapter.MenuFolderDetailAllRVAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.MenuService
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFolderDetailAllFragment : Fragment() {

    lateinit var binding: FragmentMenuFolderDetailAllBinding
    var chipItems = ArrayList<Chip>()
    var tagItems = ArrayList<String>()
    lateinit var dummyItems: ArrayList<MenuData>
    lateinit var menuItems: ArrayList<MenuData>
    lateinit var sortedMenuItems: ArrayList<MenuData>
    lateinit var rvAdapter: MenuFolderDetailAllRVAdapter

    private val retrofit = RetrofitObject.retrofit
    private val menuService = retrofit.create(MenuService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMenuFolderDetailAllBinding.inflate(layoutInflater)

        initChips()
        initSpinner()

        getMenuItems()

        initListener()
        initRVAdapter()




        return binding.root
    }

    private fun getMenuItems() {
        menuService.getMenus(
            menuTitle = "", menuTag = ArrayList<String>(), menuFolderId = 0
        ).enqueue(object : Callback<MenuArrayResponse> {
            override fun onResponse(call: Call<MenuArrayResponse>, response: Response<MenuArrayResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.response?.let {
                        menuItems = result.response
                        sortedMenuItems = result.response
                    }
                }
            }

            override fun onFailure(call: Call<MenuArrayResponse>, t: Throwable) {
                Log.d("AllMenu", t.toString())
            }

        })
    }

    private fun initRVAdapter() {
        val dummyItems = ArrayList<MenuData>()
        for (i in 1..9) {
            dummyItems.add(
                MenuData(
                    groupId = 0,
                    menuId = 0,
                    menuImgUrl = "",
                    menuPrice = 0,
                    menuTitle = "menu$i",
                    placeAddress = "address$i",
                    placeTitle = "place$i"
                ),
            )
        }

        rvAdapter =
            MenuFolderDetailAllRVAdapter(dummyItems, requireContext())
        binding.rvMfdaMenu.adapter = rvAdapter
    }

    private fun initChips() {
        if (chipItems.size > 0) {
            for (i in 0 until chipItems.size) {
                // 부모 뷰에서 제거 후 붙이기
                val parent = chipItems[i].parent as ViewGroup
                parent.removeView(chipItems[i])
                binding.cgMfda.addView(
                    chipItems[i]
                )
                tagItems.add(
                    chipItems[i].text.toString()
                )
            }
        }

        // TODO 가격 칩 추가하기
    }

    private fun initSpinner() {
        val adapter =
            MenuFolderAllFilterSpinnerAdapter<String>(requireContext(), arrayListOf("이름순", "등록순", "가격순"))
        adapter.setDropDownViewResource(R.layout.spinner_item_background)
        binding.spnMfdaFilter.adapter = adapter
        binding.spnMfdaFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                adapter.selectedPos = position
//                sortBySpinner(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun sortBySpinner(position: Int) {
        when (position) {
            0 -> { // 이름순, 이름이 같아면 가격순
                sortedMenuItems.sortWith(compareBy<MenuData> { it.menuTitle }.thenBy { it.menuPrice })
            }

            1 -> { // 등록순
                sortedMenuItems.sortBy { it.menuTitle }
            }

            2 -> { // 가격순, 가격이 같다면 이름순
                sortedMenuItems.sortWith(compareBy<MenuData> { it.menuPrice }.thenBy { it.menuTitle })
            }

            else -> return
        }
        rvAdapter.updateList(sortedMenuItems)


    }

    private fun initListener() {

        binding.ivMfdaBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.chipMfdaAll.setOnClickListener {

            val menuFolderDetailAllFilterFragment = MenuFolderDetailAllFilterFragment(this)
            if (chipItems.size > 0) {
                val bundle = Bundle()
                for (i in 0 until chipItems.size) {
                    bundle.putString("chip$i", chipItems[i].text.toString())
                }
                menuFolderDetailAllFilterFragment.arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.menu_folder_frm, menuFolderDetailAllFilterFragment)
                .addToBackStack("MenuFolderDetailAllFragment")
                .commit()
        }
    }

    // 필터 프래그먼트에서 추가
    fun addChips(context: Context, chips: ArrayList<Chip>) {
        for (i in 0 until chips.size) {
            chips[i].chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.Neutral_White)
            chips[i].chipIconTint = ContextCompat.getColorStateList(context, R.color.Neutral_Black)
            chips[i].setTextColor(ContextCompat.getColorStateList(context, R.color.Neutral_Black))
        }
        chipItems = chips
    }


}
