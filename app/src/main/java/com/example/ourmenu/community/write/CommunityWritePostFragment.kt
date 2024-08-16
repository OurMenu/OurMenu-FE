package com.example.ourmenu.community.write

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.community.write.adapter.CommunityWritePostRVAdapter
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.data.community.ArticleRequestData
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.databinding.FragmentCommunityWritePostBinding
import com.example.ourmenu.util.Utils.getTypeOf
import kotlin.math.max

class CommunityWritePostFragment : Fragment() {

    lateinit var binding: FragmentCommunityWritePostBinding
    lateinit var rvAdapter: CommunityWritePostRVAdapter
    private var menuItems = ArrayList<ArticleRequestData>()
//    var dummyItems = ArrayList<DummyMenuData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommunityWritePostBinding.inflate(layoutInflater)

//        initDummy()
        initRV()
        initListener()
        checkEnabled()


        val menuBundle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("items", getTypeOf<ArrayList<ArticleRequestData>>())
                ?: arrayListOf()
        } else {
            arguments?.getSerializable("items") as ArrayList<ArticleRequestData>
                ?: arrayListOf()
        }

        menuItems.addAll(menuBundle)

        return binding.root
    }

    private fun initListener() {
        binding.ivCwpBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnCwpOk.setOnClickListener {
            // TODO API 구현
            requireActivity().finish()
        }
    }

    private fun checkEnabled() {
        // 제목, 본문, 사진 중 하나라도 없으면 비활성화
        binding.btnCwpOk.isEnabled =
            !(binding.etCwpTitle.text.isBlank() || binding.etCwpContent.text.isBlank() || menuItems.isEmpty())
    }

    private fun initDummy() {
//        dummyItems.addAll(
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                arguments?.getSerializable("checkedItems", getTypeOf<ArrayList<DummyMenuData>>())
//                    ?: arrayListOf()
//            } else {
//                arguments?.getSerializable("checkedItems") as ArrayList<DummyMenuData>
//                    ?: arrayListOf()
//            }  // 제네릭으로 * 을 줘야 getSerializable 가능
//        )
    }

    private fun initRV() {
        rvAdapter =
            CommunityWritePostRVAdapter(menuItems, requireContext()) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.community_post_frm, CommunityWritePostGetFragment())
                    .addToBackStack("CommunityWritePostFragment")
                    .commitAllowingStateLoss()
            }

        binding.rvCommunityPost.adapter = rvAdapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCommunityPost)
    }

}
