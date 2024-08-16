package com.example.ourmenu.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourmenu.R
import com.example.ourmenu.community.adapter.CommunityFilterSpinnerAdapter
import com.example.ourmenu.community.write.CommunityWritePostActivity
import com.example.ourmenu.data.PostData
import com.example.ourmenu.databinding.FragmentCommunityBinding
import com.example.ourmenu.mypage.adapter.MypageRVAdapter
import com.example.ourmenu.util.Utils.viewGone
import com.example.ourmenu.util.Utils.viewVisible

class CommunityFragment : Fragment() {

    lateinit var binding: FragmentCommunityBinding
    lateinit var dummyItems: ArrayList<PostData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        initDummyData()
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

    private fun initDummyData() {
        dummyItems = ArrayList<PostData>()
        for (i in 1..6) {
            dummyItems.add(
                PostData(
                    "제목",
                    "가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하",
                    R.drawable.menu_sample2,
                    "베터씨",
                    "1 day ago",
                    999,
                    R.drawable.menu_sample3,
                    9,
                ),
            )
        }
    }

    private fun initListener() {
        binding.ivCommunityWrite.setOnClickListener{
            val intent = Intent(context, CommunityWritePostActivity::class.java)
            intent.putExtra("flag", "write")
            startActivity(intent)
        }
    }

    private fun initRV() {
        val adapter =
            MypageRVAdapter(dummyItems) {
                // TODO: 해당 게시물로 이동하기
                val intent = Intent(context, CommunityWritePostActivity::class.java)
                intent.putExtra("isMine", true)
                intent.putExtra("postData", it)
                intent.putExtra("flag", "post")
                startActivity(intent)
            }

        binding.rvCommunity.adapter = adapter
    }

}
