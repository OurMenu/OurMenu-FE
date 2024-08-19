package com.example.ourmenu.community.write

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.account.SignupEmailCertifyFragment
import com.example.ourmenu.community.write.adapter.CommunityWritePostRVAdapter
import com.example.ourmenu.data.DummyMenuData
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.data.account.AccountEmailResponse
import com.example.ourmenu.data.community.ArticleRequestData
import com.example.ourmenu.data.community.ArticleResponse
import com.example.ourmenu.data.community.CommunityArticleRequest
import com.example.ourmenu.data.community.CommunityResponse
import com.example.ourmenu.data.menu.data.MenuData
import com.example.ourmenu.databinding.FragmentCommunityWritePostBinding
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.AccountService
import com.example.ourmenu.retrofit.service.CommunityService
import com.example.ourmenu.util.Utils
import com.example.ourmenu.util.Utils.getTypeOf
import com.example.ourmenu.util.Utils.showToast
import com.example.ourmenu.util.Utils.viewGone
import retrofit2.Call
import retrofit2.Response
import kotlin.math.max

class CommunityWritePostFragment : Fragment() {

    lateinit var binding: FragmentCommunityWritePostBinding
    lateinit var rvAdapter: CommunityWritePostRVAdapter
    private var menuItems = ArrayList<ArticleRequestData>()
    private var bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommunityWritePostBinding.inflate(layoutInflater)

        initListener()
        checkEnabled()


        val title = arguments?.getString("title")
        if (title != null && title != "") {
            binding.etCwpTitle.setText(title)
        }

        val content = arguments?.getString("content")
        if (content != null && content != "") {
            binding.etCwpContent.setText(content)
        }

        val menuBundle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("items", getTypeOf<ArrayList<ArticleRequestData>>())
                ?: arrayListOf()
        } else {
            arguments?.getSerializable("items") as ArrayList<ArticleRequestData>
                ?: arrayListOf()
        }

        Log.d("menu", menuBundle.toString())

        menuItems.addAll(menuBundle)

        initRV()

        checkEnabled()

        return binding.root
    }

    private fun initListener() {
        binding.ivCwpBack.setOnClickListener {
            requireActivity().finish()
        }
        binding.btnCwpOk.setOnClickListener {
            // TODO API 구현
            val menuList = arguments?.getSerializable("items") as ArrayList<ArticleRequestData>
            postCommunityArticle(menuList)
            arguments?.clear()
        }
        binding.etCwpContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEnabled()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.etCwpTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEnabled()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    fun postCommunityArticle(menuList: ArrayList<ArticleRequestData>) {
        val service = RetrofitObject.retrofit.create(CommunityService::class.java)
        val call = service.postCommunityArticle(
            CommunityArticleRequest(
                binding.etCwpTitle.text.toString(),
                binding.etCwpContent.text.toString(),
                menuList
            )
        )
        call.enqueue(object : retrofit2.Callback<ArticleResponse> {
            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                if (response.isSuccessful) {
                    requireActivity().finish()
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkEnabled() {
        // 제목, 본문, 사진 중 하나라도 없으면 비활성화
        binding.btnCwpOk.isEnabled =
            !(binding.etCwpTitle.text.isBlank() || binding.etCwpContent.text.isBlank() || menuItems.isEmpty())
    }

    private fun initRV() {
        rvAdapter =
            CommunityWritePostRVAdapter(menuItems, requireContext()) {

                bundle.putSerializable("items",menuItems)
                bundle.putString("title", binding.etCwpTitle.text.toString())
                bundle.putString("content", binding.etCwpContent.text.toString())

                val communityWritePostGetFragment = CommunityWritePostGetFragment()
                communityWritePostGetFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.community_post_frm, communityWritePostGetFragment)
                    .addToBackStack("CommunityWritePostFragment")
                    .commitAllowingStateLoss()
            }

        binding.rvCommunityPost.adapter = rvAdapter
        if (menuItems.size > 0){
            binding.rvCommunityPost.scrollToPosition(menuItems.size)
        }
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCommunityPost)
    }

}
