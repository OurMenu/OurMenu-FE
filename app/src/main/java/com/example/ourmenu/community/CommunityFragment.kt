package com.example.ourmenu.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.community.adapter.CommunityFilterSpinnerAdapter
import com.example.ourmenu.community.write.CommunityWritePostActivity
import com.example.ourmenu.data.community.CommunityResponse
import com.example.ourmenu.data.community.CommunityResponseData
import com.example.ourmenu.databinding.FragmentCommunityBinding
import com.example.ourmenu.mypage.adapter.MypageRVAdapter
import com.example.ourmenu.retrofit.RetrofitObject
import com.example.ourmenu.retrofit.service.CommunityService
import retrofit2.Call
import retrofit2.Response

class CommunityFragment : Fragment() {

    lateinit var binding: FragmentCommunityBinding
    var Items: ArrayList<CommunityResponseData> = ArrayList()
    var page = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        initItem()
        initSpinner()
        initListener()
        initRV()

        return binding.root
    }


    private fun initSpinner() {
        val adapter =
            CommunityFilterSpinnerAdapter<String>(requireContext(), arrayListOf("최신순", "조회순"))
        adapter.setDropDownViewResource(R.layout.spinner_item_background)
        binding.spnCommunityFilter.adapter = adapter
        binding.spnCommunityFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                adapter.isNewest = position == 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    fun getCommunity() {
        val service = RetrofitObject.retrofit.create(CommunityService::class.java)
        val call = service.getCommunity("", page++, 5)
        call.enqueue(object : retrofit2.Callback<CommunityResponse> {
            override fun onResponse(call: Call<CommunityResponse>, response: Response<CommunityResponse>) {
                if (response.isSuccessful) {
                    for (i in response.body()?.response!!) {
                        Items.add(i)
                    }
                } else {
                    Log.d("오류", response.body().toString())
                }
            }

            override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initItem() {
        getCommunity()
    }

    fun addItem() {
        getCommunity()
        binding.rvCommunity.adapter?.notifyItemRangeInserted((page-1)*5,5)
    }

    private fun initListener() {
        binding.ivCommunityWrite.setOnClickListener {
            val intent = Intent(context, CommunityWritePostActivity::class.java)
            intent.putExtra("flag", "write")
            startActivity(intent)
        }
    }

    private fun initRV() {
        val adapter =
            MypageRVAdapter(Items,requireContext()) {
                // TODO: 해당 게시물로 이동하기
                val intent = Intent(context, CommunityWritePostActivity::class.java)
                intent.putExtra("isMine", true)
                intent.putExtra("postData", it.articleContent)
                intent.putExtra("flag", "post")
                startActivity(intent)
            }

        binding.rvCommunity.adapter = adapter
        binding.rvCommunity.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    // 스크롤이 끝났을 때 추가 데이터를 로드
                    addItem()
                }
            }
        })
    }

}
