package com.example.ourmenu.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.databinding.ItemHomeMenuSubBinding

class HomeMenuSubRVAdapter(val items: ArrayList<HomeMenuData>) : RecyclerView.Adapter<HomeMenuSubRVAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHomeMenuSubBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : HomeMenuData) {
            binding.tvItemMenuSub.text = item.menu
            binding.tvItemStoreSub.text = item.store
            // TODO Glide 추가.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeMenuSubRVAdapter.ViewHolder {
        val binding = ItemHomeMenuSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeMenuSubRVAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
