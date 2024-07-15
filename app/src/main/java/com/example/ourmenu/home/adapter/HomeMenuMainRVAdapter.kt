package com.example.ourmenu.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.databinding.ItemHomeMenuMainBinding

class HomeMenuMainRVAdapter(val items: ArrayList<HomeMenuData>, val context:Context): RecyclerView.Adapter<HomeMenuMainRVAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHomeMenuMainBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: HomeMenuData){
            binding.tvItemMenuMain.text = item.menu
            binding.tvItemStoreMain.text = item.store
            // TODO Glide 추가

            if(adapterPosition==0 || adapterPosition == items.size-1){
                binding.layoutItemHomeMenuMain.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

                val displayMetrics = context.resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels
                var mLayoutParam : RecyclerView.LayoutParams =  binding.layoutItemHomeMenuMain.layoutParams as RecyclerView.LayoutParams
                if(adapterPosition == 0)
                    mLayoutParam.leftMargin = (screenWidth - binding.layoutItemHomeMenuMain.measuredWidthAndState)/2
                else
                    mLayoutParam.rightMargin = (screenWidth - binding.layoutItemHomeMenuMain.measuredWidthAndState)/2
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeMenuMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }


}
