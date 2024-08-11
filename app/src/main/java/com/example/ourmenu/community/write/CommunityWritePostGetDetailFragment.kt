package com.example.ourmenu.community.write

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.ourmenu.R
import com.example.ourmenu.community.write.adapter.CommunityWritePostGetDetailRVAdapter
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.databinding.FragmentCommunityWritePostGetDetailBinding
import com.example.ourmenu.menu.menuFolder.post.adapter.PostMenuFolderGetDetailRVAdapter

class CommunityWritePostGetDetailFragment(val writePostFragment: CommunityWritePostFragment) : Fragment() {

    lateinit var binding: FragmentCommunityWritePostGetDetailBinding
    lateinit var dummyItems: ArrayList<DummyMenuData>
    lateinit var communityWritePostGetDetailRVAdapter: CommunityWritePostGetDetailRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityWritePostGetDetailBinding.inflate(layoutInflater)

        initDummy()
        initRV()
        initListener()
        checkButtonEnabled()






        return binding.root
    }

    private fun checkButtonEnabled() {
        // 체크된 항목이 없으면 비활성화,
        // 체크된 항목이 있으면 활성화
        binding.btnCwpgdAddMenu.isEnabled = communityWritePostGetDetailRVAdapter.checkedItems.isNotEmpty()
    }

    private fun initListener() {
        binding.ivCwpgdBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnCwpgdAddMenu.setOnClickListener {

            val bundle = Bundle().apply {
                putSerializable(
                    "checkedItems", communityWritePostGetDetailRVAdapter.checkedItems
                )
            }

            writePostFragment.apply {
                arguments = bundle
            }

            with(parentFragmentManager) {
                // 백스택 제거
                popBackStack("CommunityWritePostFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                popBackStack("CommunityWritePostGetFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                beginTransaction()
                    .replace(R.id.community_post_frm, writePostFragment)
                    .commit()
            }
        }
    }

    private fun initDummy() {
        dummyItems = ArrayList<DummyMenuData>()
        for (i in 1..5) {
            dummyItems.add(
                DummyMenuData(menu = "화산라멘", store = "화산점", address = "서울 광진구")
            )
        }
    }

    // TODO string.xml 에 저장하고 placeholder 로 사용권장.
    @SuppressLint("SetTextI18n")
    private fun initRV() {
        communityWritePostGetDetailRVAdapter = CommunityWritePostGetDetailRVAdapter(dummyItems).apply {
            setOnItemClickListener {
                checkButtonEnabled()
            }
        }
        binding.rvCwpgdList.adapter = communityWritePostGetDetailRVAdapter

        // 메뉴 갯수 조정
        binding.tvCwpgdNumber.text = "메뉴 ${dummyItems.size}개"
    }
}
