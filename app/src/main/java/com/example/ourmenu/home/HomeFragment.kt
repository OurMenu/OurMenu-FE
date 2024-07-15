package com.example.ourmenu.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.R
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.databinding.FragmentHomeBinding
import com.example.ourmenu.home.adapter.HomeMenuMainRVAdapter
import com.example.ourmenu.home.adapter.HomeMenuSubRVAdapter
import kotlin.math.max

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var dummyItems: ArrayList<HomeMenuData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initDummyData()
        initMainMenuRV()
        initSubMenuRV()

        return binding.root
    }

    private fun initSubMenuRV() {
        binding.rvHomeMenuSubFirst.adapter = HomeMenuSubRVAdapter(dummyItems)
        binding.rvHomeMenuSubSecond.adapter = HomeMenuSubRVAdapter(dummyItems)
    }


    private fun initDummyData() {
        dummyItems = ArrayList<HomeMenuData>()
        for (i in 1..6){
            dummyItems.add(
                HomeMenuData("1", "menu2", "store3")
            )
        }
    }

    private fun initMainMenuRV() {

        binding.rvHomeMenuMain.adapter = HomeMenuMainRVAdapter(dummyItems, requireContext())

        binding.rvHomeMenuMain.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    val firstPos = (binding.rvHomeMenuMain.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    val secondPos = (binding.rvHomeMenuMain.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    val selectedPos = max(firstPos,secondPos)
                    if(selectedPos!=-1){
                        val viewItem = (binding.rvHomeMenuMain.layoutManager as LinearLayoutManager).findViewByPosition(selectedPos)
                        viewItem?.run{
                            val itemMargin = (binding.rvHomeMenuMain.measuredWidth-viewItem.measuredWidth)/2
                            binding.rvHomeMenuMain.smoothScrollBy( this.x.toInt()-itemMargin , 0)
                        }

                    }
                }
            }
        })

    }
}
