package com.example.ourmenu.community.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ourmenu.R
import com.example.ourmenu.community.write.adapter.CommunityWritePostGetRVAdapter
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.databinding.FragmentCommunityWritePostGetBinding
import com.example.ourmenu.menu.iteminterface.MenuFolderItemClickListener

class CommunityWritePostGetFragment(val writePostFragment: CommunityWritePostFragment) : Fragment() {

    lateinit var binding: FragmentCommunityWritePostGetBinding
    lateinit var dummyItems: ArrayList<DummyMenuData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityWritePostGetBinding.inflate(layoutInflater)

        initDummy()
        initRV()
        initListener()



        return binding.root
    }

    private fun initListener() {
        // 뒤로가기
        binding.ivCwpgBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnCwpgAddMenu
    }

    private fun initDummy() {
        dummyItems = ArrayList<DummyMenuData>()
        for (i in 1..5) {
            dummyItems.add(
                DummyMenuData(title = "title$i", menuCount = i)
            )
        }
    }

    private fun initRV() {
        binding.rvCwpgMenuFolder.adapter = CommunityWritePostGetRVAdapter(dummyItems).apply {
            setOnItemClickListener(object : MenuFolderItemClickListener {
                // 메뉴 클릭 리스너 추가
                override fun onMenuClick(menuFolderId: Int) {
                    val communityWritePostGetDetailFragment = CommunityWritePostGetDetailFragment(writePostFragment)

                    parentFragmentManager.beginTransaction()
                        .addToBackStack("CommunityWritePostGetFragment")
                        .replace(R.id.community_post_frm, communityWritePostGetDetailFragment)
                        .commit()
                }

                override fun onEditClick() {
                }

                override fun onDeleteClick(menuFolderId: Int, position: Int) {
                }

            })
        }
    }
}
