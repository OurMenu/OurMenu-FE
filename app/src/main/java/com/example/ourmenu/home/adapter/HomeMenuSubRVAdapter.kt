package com.example.ourmenu.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourmenu.R
import com.example.ourmenu.data.onboarding.data.OnboardingMenuData
import com.example.ourmenu.databinding.ItemHomeMenuSubBinding
import com.example.ourmenu.home.iteminterface.HomeItemClickListener
import com.example.ourmenu.util.Utils
import com.example.ourmenu.util.Utils.showToast

class HomeMenuSubRVAdapter(
    val items: ArrayList<OnboardingMenuData>,
    val context: Context,
) : RecyclerView.Adapter<HomeMenuSubRVAdapter.ViewHolder>() {
    private lateinit var itemClickListener: HomeItemClickListener

    fun setOnItemClickListener(onItemClickListener: HomeItemClickListener) {
        itemClickListener = onItemClickListener
    }

    inner class ViewHolder(
        private val binding: ItemHomeMenuSubBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnboardingMenuData) {
            // 아이템 클릭 리스너 추가
            binding.root.setOnClickListener {
                if (item.userOwned) {
                    itemClickListener.onItemClick(item)
                } else {
                    showToast(context, R.drawable.ic_error, "내 메뉴판에 있는 메뉴만 볼 수 있어요!")
                }
            }
            binding.tvItemMenuSub.text = item.menuTitle
            binding.tvItemStoreSub.text = item.placeName
            // TODO Glide 추가.

            if (item.menuImgUrl == "null" || item.menuImgUrl.isNullOrEmpty()) {
                binding.sivItemMenuImageSub.setImageResource(R.drawable.default_image)
            } else {
                Glide
                    .with(context)
                    .load(item.menuImgUrl)
                    .into(binding.sivItemMenuImageSub)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HomeMenuSubRVAdapter.ViewHolder {
        val binding = ItemHomeMenuSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HomeMenuSubRVAdapter.ViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
