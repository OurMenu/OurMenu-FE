package com.example.ourmenu.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourmenu.data.HomeMenuData
import com.example.ourmenu.data.onboarding.data.OnboardingMenuData
import com.example.ourmenu.data.onboarding.data.OnboardingTagData
import com.example.ourmenu.databinding.ItemHomeMenuSubBinding
import com.example.ourmenu.home.iteminterface.HomeItemClickListener

class HomeMenuSubRVAdapter(val items: ArrayList<OnboardingMenuData>) :
    RecyclerView.Adapter<HomeMenuSubRVAdapter.ViewHolder>() {

    private lateinit var itemClickListener: HomeItemClickListener

    fun setOnItemClickListener(onItemClickListener: HomeItemClickListener) {
        itemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: ItemHomeMenuSubBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnboardingMenuData) {
            // 아이템 클릭 리스너 추가
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(item)
            }

            binding.tvItemMenuSub.text = item.menuTitle
            binding.tvItemStoreSub.text = item.placeName
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
